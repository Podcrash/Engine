package com.podcrash.api.location;

public final class CoordinateHelper {
    public static Coordinate findNormal(Coordinate... coordinates) {
        return findNormal(coordinates[0], coordinates[1], coordinates[2]);
    }

    /**
     * After
     * ax + by + cz + d = 0
     * @param coordinate1 a
     * @param coordinate2 b
     * @param coordinate3 c
     * @return
     */
    public static Coordinate findNormal(Coordinate coordinate1, Coordinate coordinate2, Coordinate coordinate3) {
        Coordinate coordinate12 = coordinate1.subtract(coordinate2);
        Coordinate coordinate13 = coordinate1.subtract(coordinate3);
        return coordinate12.crossProduct(coordinate13);
    }

    public static double[] findEquationPlane(Coordinate coordinate1, Coordinate normal) {
        double d = normal.getX() * -coordinate1.getX() +
                   normal.getY() * -coordinate1.getY() +
                   normal.getZ() * -coordinate1.getZ();
        return new double[] {normal.getX(), normal.getY(), normal.getZ(), d};
    }
}
