package agh.ics.mds.Enums;

public enum Driver {
    LEWIS_HAMILTION,
    MAX_VERSTAPPEN,
    ROBERT_KUBICA,
    None;

    public static int getDriverMaxSpeed(Driver driver) {

        return switch (driver) {
            case LEWIS_HAMILTION -> 7;
            case MAX_VERSTAPPEN -> 5;
            case ROBERT_KUBICA -> 3;
            default -> 0;
        };
    }

    public static int fastestCar(){
        return Math.max((getDriverMaxSpeed(LEWIS_HAMILTION)),
                Math.max(getDriverMaxSpeed(MAX_VERSTAPPEN),
                        (getDriverMaxSpeed(ROBERT_KUBICA))));
    }
}
