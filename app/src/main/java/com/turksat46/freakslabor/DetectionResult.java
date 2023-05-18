package com.turksat46.freakslabor;

import android.graphics.RectF;

public class DetectionResult implements Comparable<DetectionResult>{

    private final Integer id;
    private final String title;
    private final Float confidence;
    private RectF location;

    public DetectionResult(final Integer id, final String title,
                           final Float confidence, final RectF location) {
        this.id = id;
        this.title = title;
        this.confidence = confidence;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() { return title; }

    public Float getConfidence() { return confidence; }

    public RectF getLocation() {
        return new RectF(location);
    }

    public void setLocation(RectF location) {
        this.location = location;
    }

    @Override
    public int compareTo(DetectionResult o) {
        return Float.compare(o.getConfidence(), this.getConfidence());
    }

    @Override
    public String toString() {
        return "DetectionResult{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", confidence=" + confidence +
                ", location=" + location +
                '}';
    }
}
