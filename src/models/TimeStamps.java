/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author intel
 */
public abstract class TimeStamps {
    private Date createdAt = null;
    private Date updatedAt = null;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    private Date formatDate(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, formatter);
        
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        return Date.from(zonedDateTime.toInstant());
    }
    
    public void setCreatedAtFromTimeStamp(String timestamp) {
        this.createdAt = formatDate(timestamp);
    }
    
    public void setUpdatedAtFromTimeStamp(String timestamp) {
        this.updatedAt = formatDate(timestamp);
    }
    
    public String getCreatedAtDate() {
        return dateFormat.format(createdAt);
    }
    
    public String getCreatedAtTime() {
        return timeFormat.format(createdAt);
    }
    
    public String getUpdatedAtDate() {
        return dateFormat.format(updatedAt);
    }
    
    public String getUpdatedAtTime() {
        return timeFormat.format(updatedAt);
    }
}
