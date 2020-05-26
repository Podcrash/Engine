package com.podcrash.api.db.pojos.map;

public class IDPoint2Point extends Point2Point {
    private int id;

    public IDPoint2Point() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IDPoint2Point)) return false;
        if (!super.equals(o)) return false;

        IDPoint2Point that = (IDPoint2Point) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }
}
