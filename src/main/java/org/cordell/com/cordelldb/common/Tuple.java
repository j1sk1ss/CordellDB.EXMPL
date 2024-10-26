package org.cordell.com.cordelldb.common;

public record Tuple<X, Y>(X x, Y y) {

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Tuple)) return false;

        @SuppressWarnings("unchecked")
        Tuple<X, Y> other_ = (Tuple<X, Y>) other;

        return other_.x.equals(this.x) && other_.y.equals(this.y);
    }

}
