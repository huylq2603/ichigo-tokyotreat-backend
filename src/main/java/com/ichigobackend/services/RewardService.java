package com.ichigobackend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ichigo.exceptions.CustomException;
import com.ichigobackend.models.RewardTimestamp;
import com.ichigobackend.repos.RewardTimestampRepository;
import com.ichigobackend.utils.TimeUtils;

/**
 *
 * @author huylq
 */

@Service
public class RewardService {

	private RewardTimestampRepository rewardTimestampRepository;

	public RewardService(RewardTimestampRepository rewardTimestampRepository) {
		this.rewardTimestampRepository = rewardTimestampRepository;
	}

	public List<RewardTimestamp> getWeeklyRewardsAt(Integer userId, String at) throws IllegalArgumentException {

		// find start(SUNDAY) and end(SATURDAY) of the week containing the time stamp at
		LocalDateTime atDateTime = TimeUtils.ISOStringToTime(at);
		int startDateOffset = atDateTime.getDayOfWeek().getValue() % 7;
		LocalDateTime startDate = TimeUtils.toMidnight(atDateTime.minusDays(startDateOffset));
		LocalDateTime endDate = startDate.plusDays(6);

		List<RewardTimestamp> rewardList = rewardTimestampRepository.getWeeklyRewardsByUserIdAndStartEnd(userId,
				startDate, endDate);

		// generate rewards for that week if not found any
		if (rewardList.size() == 0) {
			rewardList = generateWeeklyRewards(userId, startDate);
			rewardTimestampRepository.saveAll(rewardList);

		}

		return rewardList;
	}

	public RewardTimestamp redeemAt(Integer userId, String availableAt)
			throws IllegalArgumentException, CustomException {
		LocalDateTime availableAtDateTime = TimeUtils.ISOStringToTime(availableAt);
		RewardTimestamp reward = rewardTimestampRepository.getRewardByUserIdAt(userId, availableAtDateTime);
		LocalDateTime now = LocalDateTime.now();

		if (reward == null) {
			throw new CustomException("No reward available at " + availableAt);

		} else {
			if (reward.getRedeemedAt() != null) {
				throw new CustomException("This reward has already been redeemed at "
						+ TimeUtils.timeToISOString(reward.getRedeemedAt()));

			} else {
				if (now.isBefore(reward.getAvailableAt())) {
					throw new CustomException("This reward is not available yet");

				} else if (now.isAfter(reward.getExpiresAt())) {
					throw new CustomException("This reward is already expired");

				} else {
					reward.setRedeemedAt(now);
					rewardTimestampRepository.save(reward);

				}
			}
		}

		return reward;
	}

	private List<RewardTimestamp> generateWeeklyRewards(Integer userId, LocalDateTime startDate) {

		List<RewardTimestamp> rewardList = new ArrayList<RewardTimestamp>();

		for (int i = 0; i < 7; i++) {
			RewardTimestamp reward = new RewardTimestamp(userId, startDate.plusDays(i));
			rewardList.add(reward);
		}

		return rewardList;
	}

}
