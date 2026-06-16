package model;

public class AdminConfig {
    private int lowThreshold = 0;
    private int midThreshold = 8;
    private int highThreshold = 10;
    private int maxThreshold = 12;
    private boolean autoReportEnabled = false;
    private int reportHour = 2;
    private double aiSkillWeight = 0.5;
    private double aiAvailabilityWeight = 0.3;
    private double aiHistoryWeight = 0.2;

    public int getLowThreshold() { return lowThreshold; }
    public void setLowThreshold(int lowThreshold) { this.lowThreshold = lowThreshold; }

    public int getMidThreshold() { return midThreshold; }
    public void setMidThreshold(int midThreshold) { this.midThreshold = midThreshold; }

    public int getHighThreshold() { return highThreshold; }
    public void setHighThreshold(int highThreshold) { this.highThreshold = highThreshold; }

    public int getMaxThreshold() { return maxThreshold; }
    public void setMaxThreshold(int maxThreshold) { this.maxThreshold = maxThreshold; }

    public boolean isAutoReportEnabled() { return autoReportEnabled; }
    public void setAutoReportEnabled(boolean autoReportEnabled) { this.autoReportEnabled = autoReportEnabled; }

    public int getReportHour() { return reportHour; }
    public void setReportHour(int reportHour) { this.reportHour = reportHour; }

    public double getAiSkillWeight() { return aiSkillWeight; }
    public void setAiSkillWeight(double aiSkillWeight) { this.aiSkillWeight = aiSkillWeight; }

    public double getAiAvailabilityWeight() { return aiAvailabilityWeight; }
    public void setAiAvailabilityWeight(double aiAvailabilityWeight) { this.aiAvailabilityWeight = aiAvailabilityWeight; }

    public double getAiHistoryWeight() { return aiHistoryWeight; }
    public void setAiHistoryWeight(double aiHistoryWeight) { this.aiHistoryWeight = aiHistoryWeight; }
}
