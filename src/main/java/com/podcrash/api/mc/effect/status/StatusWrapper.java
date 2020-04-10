package com.podcrash.api.mc.effect.status;

/**
 * This is used to apply statuses easier
 */
public final class StatusWrapper {
    private final Status status;
    private final float duration;
    private final int potency;
    private final boolean ambient;
    private final boolean override;

    public StatusWrapper(Status status, float duration, int potency, boolean ambient, boolean override) {
        this.status = status;
        this.duration = duration;
        this.potency = potency;
        this.ambient = ambient;
        this.override = override;
    }

    /**
     * Wrapper for applying statuses
     * @param status see {@link StatusApplier#applyStatus(StatusWrapper)}
     * @param duration
     * @param potency
     * @param ambient
     */
    public StatusWrapper(Status status, float duration, int potency, boolean ambient) {
        this(status, duration, potency, ambient, false);
    }

    public Status getStatus() {
        return status;
    }

    public float getDuration() {
        return duration;
    }

    public int getPotency() {
        return potency;
    }

    public boolean isAmbience() {
        return ambient;
    }
    public boolean isOverride() {
        return override;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatusWrapper{");
        sb.append("status=").append(status);
        sb.append(", duration=").append(duration);
        sb.append(", potency=").append(potency);
        sb.append(", ambient=").append(ambient);
        sb.append(", override=").append(override);
        sb.append(", ambience=").append(isAmbience());
        sb.append('}');
        return sb.toString();
    }
}
