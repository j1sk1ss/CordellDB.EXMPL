package org.cordell.com.cordelldb.common;

public record Triple<X, Y, Z>(X x, Y y, Z z) {

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Triple)) return false;

        @SuppressWarnings("unchecked")
        Triple<X, Y, Z> other_ = (Triple<X, Y, Z>) other;

        return other_.x.equals(this.x) && other_.y.equals(this.y) && other_.z.equals(this.z);
    }

}
