package model;

public class Option {
    private long optionId;
    private long gropId;
    private String optionName;
    private int extraPrice;
    private int displayOrder;

    public Option(long optionId, long gropId, String optionName, int extraPrice) {
        this.optionId = optionId;
        this.gropId = gropId;
        this.optionName = optionName;
        this.extraPrice = extraPrice;
    }

    public long getOptionId() {
        return optionId;
    }

    public long getGropId() {
        return gropId;
    }


    public String getOptionName() {
        return optionName;
    }

    public int getExtraPrice() {
        return extraPrice;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public String toString() {
        return String.format("%s (+%,d원)", optionName, extraPrice);
    }

}
