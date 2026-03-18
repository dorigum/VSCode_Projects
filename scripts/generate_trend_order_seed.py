#!/usr/bin/env python3
"""
Generate 3 years of trend-aware cafe order seed SQL.

The script reads the current MySQL metadata from this project DB and emits
INSERT/UPDATE statements for:
- ORDERS
- ORDER_ITEM
- ORDER_ITEM_OPTION
- POINT_HISTORY
- MEMBER point_balance adjustments

Design goals:
- visible long-term revenue growth
- weekday / weekend traffic differences
- seasonality by month
- hourly peaks (morning, lunch, evening)
- random variation shaped by Gaussian noise rather than flat uniform noise

Dependencies:
- PyMySQL or mysql-connector-python
"""

from __future__ import annotations

import argparse
import math
import random
import re
from collections import defaultdict
from dataclasses import dataclass, field
from datetime import date, datetime, time, timedelta
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Sequence, Tuple
from urllib.parse import urlparse

try:
    import pymysql  # type: ignore
except ImportError:
    pymysql = None

try:
    import mysql.connector  # type: ignore
except ImportError:
    mysql = None  # type: ignore


ROOT = Path(__file__).resolve().parents[1]
DBINFO_PATH = ROOT / "resources" / "dbinfo.properties"


@dataclass
class MenuOption:
    option_id: int
    group_id: int
    option_name: str
    extra_price: int
    display_order: int


@dataclass
class MenuRecord:
    menu_id: int
    category_id: int
    category_name: str
    menu_name: str
    base_price: int
    is_available: bool
    options_by_group: Dict[int, List[MenuOption]] = field(default_factory=dict)
    popularity_weight: float = 1.0


@dataclass
class MemberRecord:
    member_id: int
    point_balance: int


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Generate trend-based cafe order seed SQL.")
    parser.add_argument("--output", default="generated_order_seed_3y.sql", help="Output SQL file path")
    parser.add_argument("--start-date", default=None, help="Seed start date YYYY-MM-DD")
    parser.add_argument("--years", type=int, default=3, help="Number of years to generate")
    parser.add_argument("--daily-mean", type=float, default=200.0, help="Target mean daily order count in first year")
    parser.add_argument("--annual-growth", type=float, default=0.18, help="Annual growth rate, e.g. 0.18 = 18%%")
    parser.add_argument("--member-ratio", type=float, default=0.78, help="Probability that an order belongs to a member")
    parser.add_argument("--seed", type=int, default=20260316, help="Random seed for reproducibility")
    return parser.parse_args()


def load_dbinfo(path: Path) -> Dict[str, str]:
    if not path.exists():
        raise FileNotFoundError(f"dbinfo.properties not found: {path}")

    props: Dict[str, str] = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        stripped = line.strip()
        if not stripped or stripped.startswith("#") or "=" not in stripped:
            continue
        key, value = stripped.split("=", 1)
        props[key.strip()] = value.strip()
    return props


def open_connection(props: Dict[str, str]):
    url = props["db.url"]
    user = props["db.user"]
    password = props["db.password"]

    parsed = urlparse(url.replace("jdbc:", "", 1))
    host = parsed.hostname or "localhost"
    port = parsed.port or 3306
    database = parsed.path.lstrip("/")

    if pymysql is not None:
        return pymysql.connect(  # type: ignore[return-value]
            host=host,
            port=port,
            user=user,
            password=password,
            database=database,
            charset="utf8mb4",
            autocommit=False,
        )

    if mysql is not None:  # type: ignore[name-defined]
        return mysql.connector.connect(  # type: ignore[attr-defined]
            host=host,
            port=port,
            user=user,
            password=password,
            database=database,
        )

    raise RuntimeError(
        "PyMySQL or mysql-connector-python is required. "
        "Install one of them, for example: pip install pymysql"
    )


def fetch_all(cursor, sql: str, params: Sequence[object] | None = None) -> List[dict]:
    cursor.execute(sql, params or ())
    rows = cursor.fetchall()
    if rows and not isinstance(rows[0], dict):
        columns = [col[0] for col in cursor.description]
        return [dict(zip(columns, row)) for row in rows]
    return rows


def fetch_scalar(cursor, sql: str) -> int:
    cursor.execute(sql)
    row = cursor.fetchone()
    if isinstance(row, dict):
        value = next(iter(row.values()))
    else:
        value = row[0]
    return int(value or 0)


def load_members(cursor) -> List[MemberRecord]:
    rows = fetch_all(
        cursor,
        "SELECT member_id, point_balance FROM MEMBER WHERE role = 'USER' ORDER BY member_id"
    )
    return [MemberRecord(member_id=int(r["member_id"]), point_balance=int(r["point_balance"] or 0)) for r in rows]


def load_menus(cursor) -> List[MenuRecord]:
    menu_rows = fetch_all(
        cursor,
        """
        SELECT m.menu_id, m.category_id, c.category_name, m.menu_name, m.price, m.is_available
        FROM MENU m
        JOIN CATEGORY c ON c.category_id = m.category_id
        WHERE m.is_available = 1
        ORDER BY m.menu_id
        """
    )

    option_rows = fetch_all(
        cursor,
        """
        SELECT DISTINCT
            m.menu_id,
            og.group_id,
            mo.option_id,
            mo.option_name,
            mo.extra_price,
            mo.display_order
        FROM MENU m
        LEFT JOIN MENU_OPTION_GROUP mog ON mog.menu_id = m.menu_id
        LEFT JOIN CATEGORY_OPTION_GROUP cog ON cog.category_id = m.category_id
        JOIN OPTION_GROUP og ON og.group_id = COALESCE(mog.group_id, cog.group_id)
        JOIN MENU_OPTION mo ON mo.group_id = og.group_id
        WHERE m.is_available = 1
        ORDER BY m.menu_id, og.group_id, mo.display_order
        """
    )

    option_index: Dict[int, Dict[int, List[MenuOption]]] = defaultdict(lambda: defaultdict(list))
    for row in option_rows:
        menu_id = int(row["menu_id"])
        group_id = int(row["group_id"])
        option_index[menu_id][group_id].append(
            MenuOption(
                option_id=int(row["option_id"]),
                group_id=group_id,
                option_name=str(row["option_name"]),
                extra_price=int(row["extra_price"] or 0),
                display_order=int(row["display_order"] or 0),
            )
        )

    menus: List[MenuRecord] = []
    for row in menu_rows:
        menu = MenuRecord(
            menu_id=int(row["menu_id"]),
            category_id=int(row["category_id"]),
            category_name=str(row["category_name"]),
            menu_name=str(row["menu_name"]),
            base_price=int(row["price"]),
            is_available=bool(row["is_available"]),
            options_by_group=dict(option_index.get(int(row["menu_id"]), {})),
        )
        menu.popularity_weight = infer_menu_weight(menu)
        menus.append(menu)
    return menus


def infer_menu_weight(menu: MenuRecord) -> float:
    name = menu.menu_name
    weight = 1.0

    if any(keyword in name for keyword in ("아메리카노", "라떼", "카페", "콜드브루")):
        weight += 1.4
    if any(keyword in name for keyword in ("프라푸치노", "스무디", "에이드")):
        weight += 0.9
    if any(keyword in name for keyword in ("케이크", "쿠키", "베이글", "샌드")):
        weight += 0.7
    if "디카페인" in name:
        weight += 0.2
    if "시즌" in name:
        weight += 0.3
    return weight


def pick_weighted(items: Sequence, weights: Sequence[float]):
    return random.choices(list(items), weights=list(weights), k=1)[0]


def season_factor(day: date) -> float:
    month = day.month
    if month in (12, 1, 2):
        return 1.08
    if month in (6, 7, 8):
        return 1.16
    if month in (3, 4, 5):
        return 0.98
    return 1.03


def weekday_factor(day: date) -> float:
    factors = {
        0: 0.88,  # Monday
        1: 0.93,
        2: 0.97,
        3: 1.02,
        4: 1.10,
        5: 1.28,
        6: 1.18,
    }
    return factors[day.weekday()]


def growth_factor(day: date, start_day: date, annual_growth: float) -> float:
    elapsed_days = max(0, (day - start_day).days)
    elapsed_years = elapsed_days / 365.25
    return math.pow(1.0 + annual_growth, elapsed_years)


def gaussian_noise(stddev: float = 0.11) -> float:
    return max(0.65, random.gauss(1.0, stddev))


def daily_order_count(day: date, start_day: date, daily_mean: float, annual_growth: float) -> int:
    raw = daily_mean
    raw *= growth_factor(day, start_day, annual_growth)
    raw *= season_factor(day)
    raw *= weekday_factor(day)

    # Yearly cyclical wave gives a softer seasonal curve than month-only branching.
    doy_angle = (day.timetuple().tm_yday / 365.25) * 2.0 * math.pi
    raw *= 1.0 + 0.07 * math.sin(doy_angle - math.pi / 3.0)
    raw *= gaussian_noise(0.12)
    return max(35, int(round(raw)))


def hourly_weights() -> List[Tuple[int, float]]:
    weights: List[Tuple[int, float]] = []
    for hour in range(24):
        morning = gaussian_pdf(hour, mean=8.5, stddev=1.4) * 2.8
        lunch = gaussian_pdf(hour, mean=12.3, stddev=1.5) * 2.0
        afternoon = gaussian_pdf(hour, mean=15.0, stddev=1.8) * 1.1
        evening = gaussian_pdf(hour, mean=19.2, stddev=1.8) * 1.6
        base = 0.02 if 7 <= hour <= 21 else 0.003
        weights.append((hour, base + morning + lunch + afternoon + evening))
    total = sum(weight for _, weight in weights)
    return [(hour, weight / total) for hour, weight in weights]


def gaussian_pdf(x: float, mean: float, stddev: float) -> float:
    exponent = -((x - mean) ** 2) / (2 * stddev * stddev)
    return math.exp(exponent)


def category_weight(category_name: str, order_day: date, hour: int) -> float:
    name = category_name.strip().lower()
    month = order_day.month

    coffee = "커피" in name and "논" not in name
    non_coffee = "논커피" in name or "non" in name
    dessert = "디저트" in name or "dessert" in name

    weight = 1.0
    if coffee:
        weight *= 1.25 if hour < 11 else 1.05
        if month in (11, 12, 1, 2):
            weight *= 1.30
        elif month in (6, 7, 8):
            weight *= 0.88
    elif non_coffee:
        weight *= 0.92 if hour < 10 else 1.08
        if month in (6, 7, 8):
            weight *= 1.38
        elif month in (11, 12, 1, 2):
            weight *= 0.82
    elif dessert:
        weight *= 0.95 if hour < 10 else 1.15
        if month in (3, 4, 5):
            weight *= 1.05
        elif month in (12, 1):
            weight *= 1.10
    return weight


def build_category_index(menus: Sequence[MenuRecord]) -> Dict[str, List[MenuRecord]]:
    index: Dict[str, List[MenuRecord]] = defaultdict(list)
    for menu in menus:
        index[menu.category_name].append(menu)
    return index


def choose_menu(category_menus: Sequence[MenuRecord]) -> MenuRecord:
    weights = [menu.popularity_weight * gaussian_noise(0.08) for menu in category_menus]
    return pick_weighted(category_menus, weights)


def choose_options(menu: MenuRecord) -> Tuple[List[int], int]:
    selected: List[int] = []
    extra_price = 0

    for options in menu.options_by_group.values():
        if not options:
            continue

        ordered = sorted(options, key=lambda item: (item.display_order, item.option_id))
        base_weights = []
        for idx, option in enumerate(ordered):
            weight = 1.5 if idx == 0 else 1.0
            if option.extra_price == 0:
                weight *= 1.3
            if option.extra_price > 1000:
                weight *= 0.7
            base_weights.append(weight)

        chosen = pick_weighted(ordered, base_weights)
        selected.append(chosen.option_id)
        extra_price += chosen.extra_price

    return selected, extra_price


def choose_order_timestamp(order_day: date, hour_distribution: Sequence[Tuple[int, float]]) -> datetime:
    hour = pick_weighted([h for h, _ in hour_distribution], [w for _, w in hour_distribution])
    minute = min(59, max(0, int(random.gauss(25, 14))))
    second = random.randint(0, 59)
    return datetime.combine(order_day, time(hour=hour, minute=minute, second=second))


def choose_member(members: Sequence[MemberRecord], member_ratio: float) -> Optional[MemberRecord]:
    if not members or random.random() > member_ratio:
        return None
    weights = [1.0 + math.log1p(max(0, member.point_balance)) / 15.0 for member in members]
    return pick_weighted(members, weights)


def choose_line_count() -> int:
    return pick_weighted([1, 2, 3, 4], [0.54, 0.28, 0.13, 0.05])


def choose_quantity(category_name: str) -> int:
    if "디저트" in category_name:
        return pick_weighted([1, 2, 3], [0.72, 0.22, 0.06])
    return pick_weighted([1, 2], [0.87, 0.13])


def next_existing_ids(cursor) -> Tuple[int, int, int]:
    next_order_id = fetch_scalar(cursor, "SELECT COALESCE(MAX(order_id), 0) FROM ORDERS") + 1
    next_order_item_id = fetch_scalar(cursor, "SELECT COALESCE(MAX(order_item_id), 0) FROM ORDER_ITEM") + 1
    try:
        next_history_id = fetch_scalar(cursor, "SELECT COALESCE(MAX(history_id), 0) FROM POINT_HISTORY") + 1
    except Exception:
        next_history_id = 1
    return next_order_id, next_order_item_id, next_history_id


def sql_quote(value: object) -> str:
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "1" if value else "0"
    if isinstance(value, (int, float)):
        return str(value)
    if isinstance(value, datetime):
        return "'" + value.strftime("%Y-%m-%d %H:%M:%S") + "'"
    text = str(value).replace("\\", "\\\\").replace("'", "''")
    return "'" + text + "'"


def reason_text(order_id: int, earned: bool) -> str:
    return f"주문 결제 적립 (주문번호: {order_id})" if earned else f"주문 시 포인트 사용 (주문번호: {order_id})"


def main() -> None:
    args = parse_args()
    random.seed(args.seed)

    props = load_dbinfo(DBINFO_PATH)
    conn = open_connection(props)

    if pymysql is not None and isinstance(conn, pymysql.connections.Connection):  # type: ignore[attr-defined]
        cursor = conn.cursor(pymysql.cursors.DictCursor)  # type: ignore[attr-defined]
    else:
        cursor = conn.cursor(dictionary=True)

    try:
        members = load_members(cursor)
        menus = load_menus(cursor)
        next_order_id, next_order_item_id, next_history_id = next_existing_ids(cursor)
    finally:
        cursor.close()
        conn.close()

    if not menus:
        raise RuntimeError("No available menus found. Seed menus first.")

    if args.start_date:
        start_day = datetime.strptime(args.start_date, "%Y-%m-%d").date()
    else:
        today = date.today()
        start_day = today.replace(year=today.year - args.years)

    end_day = start_day + timedelta(days=int(round(365.25 * args.years)) - 1)
    hour_distribution = hourly_weights()
    category_index = build_category_index(menus)
    category_names = list(category_index.keys())
    member_balance_delta: Dict[int, int] = defaultdict(int)

    lines: List[str] = []
    lines.append("-- Auto-generated trend order seed")
    lines.append(f"-- start_date={start_day.isoformat()} end_date={end_day.isoformat()} seed={args.seed}")
    lines.append("SET NAMES utf8mb4;")
    lines.append("START TRANSACTION;")

    order_id = next_order_id
    order_item_id = next_order_item_id
    history_id = next_history_id
    current_day = start_day

    while current_day <= end_day:
        today_orders = daily_order_count(current_day, start_day, args.daily_mean, args.annual_growth)

        for _ in range(today_orders):
            order_time = choose_order_timestamp(current_day, hour_distribution)
            member = choose_member(members, args.member_ratio)

            category_weights = [
                category_weight(category_name, current_day, order_time.hour) for category_name in category_names
            ]

            line_count = choose_line_count()
            chosen_lines = []
            subtotal = 0

            for _line_no in range(line_count):
                category_name = pick_weighted(category_names, category_weights)
                menu = choose_menu(category_index[category_name])
                quantity = choose_quantity(category_name)
                selected_option_ids, extra_price = choose_options(menu)
                unit_price = menu.base_price + extra_price
                item_total = unit_price * quantity
                subtotal += item_total

                chosen_lines.append(
                    (
                        menu,
                        quantity,
                        unit_price,
                        selected_option_ids,
                    )
                )

            point_used = 0
            point_earned = 0
            if member is not None:
                available = member.point_balance + member_balance_delta[member.member_id]
                if available > 1500 and subtotal >= 4500 and random.random() < 0.22:
                    usage_target = min(available, int(subtotal * random.uniform(0.08, 0.32)))
                    point_used = max(0, (usage_target // 100) * 100)
                point_earned = max(0, (subtotal - point_used) // 10)
                member_balance_delta[member.member_id] += point_earned - point_used

            lines.append(
                "INSERT INTO ORDERS (order_id, member_id, total_amount, point_used, point_earned, status, order_date) VALUES "
                f"({order_id}, {sql_quote(member.member_id if member else None)}, {subtotal}, {point_used}, {point_earned}, 'COMPLETED', {sql_quote(order_time)});"
            )

            for menu, quantity, unit_price, selected_option_ids in chosen_lines:
                lines.append(
                    "INSERT INTO ORDER_ITEM (order_item_id, order_id, menu_id, quantity, unit_price, menu_name_snapshot, category_name_snapshot) VALUES "
                    f"({order_item_id}, {order_id}, {menu.menu_id}, {quantity}, {unit_price}, {sql_quote(menu.menu_name)}, {sql_quote(menu.category_name)});"
                )
                for option_id in selected_option_ids:
                    lines.append(
                        f"INSERT INTO ORDER_ITEM_OPTION (order_item_id, option_id) VALUES ({order_item_id}, {option_id});"
                    )
                order_item_id += 1

            if member is not None and point_used > 0:
                lines.append(
                    "INSERT INTO POINT_HISTORY (history_id, member_id, amount, reason, created_at) VALUES "
                    f"({history_id}, {member.member_id}, {-point_used}, {sql_quote(reason_text(order_id, earned=False))}, {sql_quote(order_time)});"
                )
                history_id += 1

            if member is not None and point_earned > 0:
                lines.append(
                    "INSERT INTO POINT_HISTORY (history_id, member_id, amount, reason, created_at) VALUES "
                    f"({history_id}, {member.member_id}, {point_earned}, {sql_quote(reason_text(order_id, earned=True))}, {sql_quote(order_time)});"
                )
                history_id += 1

            order_id += 1

        current_day += timedelta(days=1)

    for member_id, delta in sorted(member_balance_delta.items()):
        if delta == 0:
            continue
        lines.append(f"UPDATE MEMBER SET point_balance = point_balance + ({delta}) WHERE member_id = {member_id};")

    lines.append("COMMIT;")

    output_path = ROOT / args.output
    output_path.write_text("\n".join(lines) + "\n", encoding="utf-8")

    print(f"Generated SQL: {output_path}")
    print(f"Orders: {order_id - next_order_id}")
    print(f"Order items: {order_item_id - next_order_item_id}")
    print(f"Point history rows: {history_id - next_history_id}")


if __name__ == "__main__":
    main()
