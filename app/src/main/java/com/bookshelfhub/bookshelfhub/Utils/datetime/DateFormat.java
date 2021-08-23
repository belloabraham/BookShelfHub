package com.bookshelfhub.bookshelfhub.Utils.datetime;

public enum DateFormat {
    MM_DD_YYYY,
    DD_MM_YYYY;

    public String getValue() {
        switch (this) {
            case MM_DD_YYYY:
                return "MMddyyyy";
            case DD_MM_YYYY:
                return "ddMMyyyy";
        }
        throw new IllegalArgumentException("Not value available for this DateFormat: " + this);
    }

    public String getCompleteFormatValue() {
        switch (this) {
            case MM_DD_YYYY:
                return "MMM dd yyyy";
            case DD_MM_YYYY:
                return "dd MMM yyyy";
        }
        throw new IllegalArgumentException("Not value available for this DateFormat: " + this);
    }

    public int getAttrValue() {
        switch (this) {
            case MM_DD_YYYY:
                return 1;
            case DD_MM_YYYY:
                return 2;
        }
        throw new IllegalArgumentException("Not value available for this DateFormat: " + this);
    }

    public static DateFormat fromValue(int value) {
        switch (value) {
            case 1:
                return MM_DD_YYYY;
            case 2:
                return DD_MM_YYYY;
        }
        throw new IllegalArgumentException("This value is not supported for DateFormat: " + value);
    }
}
