package dk.silverbullet.telemed.device.andweightscale;

public class Weight {
    public static enum Unit {
        KG("kg"), LBS("lbs.");

        private final String name;

        Unit(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    };

    private final float weight;
    private final Unit unit;

    public Weight(float weight, Unit unit) {
        this.weight = weight;
        this.unit = unit;
    }

    public float getWeight() {
        return weight;
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + Float.floatToIntBits(weight);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Weight other = (Weight) obj;
        if (unit != other.unit)
            return false;
        if (Float.floatToIntBits(weight) != Float.floatToIntBits(other.weight))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Weight [weight=" + weight + ", unit=" + unit + "]";
    }
}
