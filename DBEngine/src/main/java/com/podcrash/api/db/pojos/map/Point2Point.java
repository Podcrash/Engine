package com.podcrash.api.db.pojos.map;

public class Point2Point {
    private Point point1;
    private Point point2;

    public Point getPoint1() {
        return point1;
    }

    public void setPoint1(Point point1) {
        this.point1 = point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point2Point that = (Point2Point) o;

        if (point1 != null ? !point1.equals(that.point1) : that.point1 != null) return false;
        return point2 != null ? point2.equals(that.point2) : that.point2 == null;
    }

    @Override
    public int hashCode() {
        int result = point1 != null ? point1.hashCode() : 0;
        result = 31 * result + (point2 != null ? point2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Point2Point{");
        sb.append("point1=").append(point1);
        sb.append(", point2=").append(point2);
        sb.append('}');
        return sb.toString();
    }
}
