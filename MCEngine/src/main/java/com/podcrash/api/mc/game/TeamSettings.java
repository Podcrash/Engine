package com.podcrash.api.mc.game;

public final class TeamSettings {

    private int capacity;
    private int min;
    private int max;
    private TeamEnum[] teamColors;

    public TeamSettings() {
        this.capacity = 3;
        this.min = 1;
        this.max = 5;

        this.teamColors = new TeamEnum[]{TeamEnum.RED, TeamEnum.BLUE};
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public TeamEnum[] getTeamColors() {
        return teamColors;
    }

    public static class Builder {
        private int capacity;
        private int min;
        private int max;
        private TeamEnum[] teamColors;

        public Builder setCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder setMin(int min) {
            this.min = min;
            return this;
        }

        public Builder setMax(int max) {
            this.max = max;
            return this;
        }

        public Builder setTeamColors(TeamEnum... teamColors) {
            this.teamColors = teamColors;
            return this;
        }

        public TeamSettings build() {
            TeamSettings settings = new TeamSettings();
            settings.capacity = this.capacity;
            settings.min = this.min;
            settings.max = this.max;
            settings.teamColors = this.teamColors;

            return settings;
        }
    }

}
