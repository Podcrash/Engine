package com.podcrash.api.mc.game;

public final class TeamSettings {

    private int min;
    private int max;
    private TeamEnum[] teamColors;

    public TeamSettings() {
        this.min = 1;
        this.max = 5;

        this.teamColors = new TeamEnum[]{TeamEnum.RED, TeamEnum.BLUE};
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
        private int min;
        private int max;
        private TeamEnum[] teamColors;


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
            settings.min = this.min;
            settings.max = this.max;
            settings.teamColors = this.teamColors;

            return settings;
        }
    }

}
