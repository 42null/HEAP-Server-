package com.heap.server.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "daily_details")
public class DailyDetails {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "notify_time")
    private LocalTime notifyTime;

    @Column(name = "repeats_monday", nullable = false)
    private boolean repeatsMonday;
    @Column(name = "repeats_tuesday", nullable = false)
    private boolean repeatsTuesday;
    @Column(name = "repeats_wednesday", nullable = false)
    private boolean repeatsWednesday;
    @Column(name = "repeats_thursday", nullable = false)
    private boolean repeatsThursday;
    @Column(name = "repeats_friday", nullable = false)
    private boolean repeatsFriday;
    @Column(name = "repeats_saturday", nullable = false)
    private boolean repeatsSaturday;
    @Column(name = "repeats_sunday", nullable = false)
    private boolean repeatsSunday;

    @Column(name = "streak_count", nullable = false)
    private int streakCount = 0;

    @Column(name = "last_completed_date")
    private LocalDate lastCompletedDate;

    // --- getters / setters ---

    public Long getItemId() { return itemId; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public LocalTime getNotifyTime() { return notifyTime; }
    public void setNotifyTime(LocalTime notifyTime) { this.notifyTime = notifyTime; }

    public boolean isRepeatsMonday() { return repeatsMonday; }
    public void setRepeatsMonday(boolean v) { this.repeatsMonday = v; }

    public boolean isRepeatsTuesday() { return repeatsTuesday; }
    public void setRepeatsTuesday(boolean v) { this.repeatsTuesday = v; }

    public boolean isRepeatsWednesday() { return repeatsWednesday; }
    public void setRepeatsWednesday(boolean v) { this.repeatsWednesday = v; }

    public boolean isRepeatsThursday() { return repeatsThursday; }
    public void setRepeatsThursday(boolean v) { this.repeatsThursday = v; }

    public boolean isRepeatsFriday() { return repeatsFriday; }
    public void setRepeatsFriday(boolean v) { this.repeatsFriday = v; }

    public boolean isRepeatsSaturday() { return repeatsSaturday; }
    public void setRepeatsSaturday(boolean v) { this.repeatsSaturday = v; }

    public boolean isRepeatsSunday() { return repeatsSunday; }
    public void setRepeatsSunday(boolean v) { this.repeatsSunday = v; }

    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }

    public LocalDate getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(LocalDate d) { this.lastCompletedDate = d; }
}
