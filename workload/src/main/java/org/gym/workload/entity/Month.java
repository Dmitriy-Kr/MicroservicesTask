package org.gym.workload.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "w_month")
public class Month {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "month_id")
    private Long id;

    @Column(name = "month_number")
    private int monthNumber;

    @Column(name = "training_summary_duration")
    private int trainingSummaryDuration;

    @ManyToOne
    @JoinColumn(name = "year_id")
    private Year year;

    public Month() {
    }

    public void increaseDurationBy(int number){
      this.trainingSummaryDuration += number;
    }

    public void decreaseDurationBy(int number){
        this.trainingSummaryDuration = number > this.trainingSummaryDuration ? 0 : this.trainingSummaryDuration - number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getTrainingSummaryDuration() {
        return trainingSummaryDuration;
    }

    public void setTrainingSummaryDuration(int trainingSummaryDuration) {
        this.trainingSummaryDuration = trainingSummaryDuration;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }
}
