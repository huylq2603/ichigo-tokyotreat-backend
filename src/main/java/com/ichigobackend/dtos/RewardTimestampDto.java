package com.ichigobackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ichigobackend.models.RewardTimestamp;
import com.ichigobackend.utils.TimeUtils;

/**
 * 
 * @author huylq
 *
 */

public class RewardTimestampDto {

	@JsonIgnore
	private Integer id;
	
	@JsonIgnore
	private Integer userId;
	
	private String availableAt;

	private String redeemedAt;

	private String expiresAt;
	
	public String getAvailableAt() {
		return availableAt;
	}

	public void setAvailableAt(String availableAt) {
		this.availableAt = availableAt;
	}

	public String getRedeemedAt() {
		return redeemedAt;
	}

	public void setRedeemedAt(String redeemedAt) {
		this.redeemedAt = redeemedAt;
	}

	public String getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(String expiresAt) {
		this.expiresAt = expiresAt;
	}

	public RewardTimestampDto(String availableAt, String redeemedAt, String expiresAt) {
		this.availableAt = availableAt;
		this.redeemedAt = redeemedAt;
		this.expiresAt = expiresAt;
	}

	public static RewardTimestampDto toDto(RewardTimestamp model) {
		if (model == null) return null;
		
		return new RewardTimestampDto(TimeUtils.timeToISOString(model.getAvailableAt()), 
				TimeUtils.timeToISOString(model.getRedeemedAt()), 
				TimeUtils.timeToISOString(model.getExpiresAt()));
	}

}
