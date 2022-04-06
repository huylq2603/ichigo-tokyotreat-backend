package com.ichigobackend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ichigo.exceptions.CustomException;
import com.ichigobackend.models.RewardTimestamp;
import com.ichigobackend.repos.RewardTimestampRepository;
import com.ichigobackend.utils.TimeUtils;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

	@Mock
	private RewardTimestampRepository rewardTimestampRepository;
	private RewardService underTest;

	@BeforeEach
	void setUp() {
		underTest = new RewardService(rewardTimestampRepository);
	}

	@Test
	void shouldGetWeeklyRewardsAt_HaveData() {
		// given
		int userId = 1;
		String now = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));

		LocalDateTime sun = LocalDateTime.of(2022, 4, 3, 0, 0, 0);
		LocalDateTime mon = LocalDateTime.of(2022, 4, 4, 0, 0, 0);
		LocalDateTime tue = LocalDateTime.of(2022, 4, 5, 0, 0, 0);
		LocalDateTime wed = LocalDateTime.of(2022, 4, 6, 0, 0, 0);
		LocalDateTime thu = LocalDateTime.of(2022, 4, 7, 0, 0, 0);
		LocalDateTime fri = LocalDateTime.of(2022, 4, 8, 0, 0, 0);
		LocalDateTime sat = LocalDateTime.of(2022, 4, 9, 0, 0, 0);

		List<RewardTimestamp> rewardList = new ArrayList<RewardTimestamp>();
		for (int i = 0; i < 7; i++) {
			RewardTimestamp reward = new RewardTimestamp(userId, sun.plusDays(i));
			rewardList.add(reward);
		}

		BDDMockito.given(rewardTimestampRepository.getWeeklyRewardsByUserIdAndStartEnd(ArgumentMatchers.any(),
				ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(rewardList);

		// when
		List<RewardTimestamp> expected = underTest.getWeeklyRewardsAt(userId, now);

		// then
		assertThat(expected.size()).isEqualTo(7);
		assertThat(expected.get(0).getAvailableAt()).isEqualTo(sun);
		assertThat(expected.get(1).getAvailableAt()).isEqualTo(mon);
		assertThat(expected.get(2).getAvailableAt()).isEqualTo(tue);
		assertThat(expected.get(3).getAvailableAt()).isEqualTo(wed);
		assertThat(expected.get(4).getAvailableAt()).isEqualTo(thu);
		assertThat(expected.get(5).getAvailableAt()).isEqualTo(fri);
		assertThat(expected.get(6).getAvailableAt()).isEqualTo(sat);

	}

	@Test
	void shouldGetWeeklyRewardsAt_NotHaveData() {
		// given
		int userId = 1;
		String now = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));

		BDDMockito.given(rewardTimestampRepository.getWeeklyRewardsByUserIdAndStartEnd(ArgumentMatchers.any(),
				ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(Collections.emptyList());

		// when
		List<RewardTimestamp> expected = underTest.getWeeklyRewardsAt(userId, now);

		// then
		ArgumentCaptor<List> rewardListCaptor = ArgumentCaptor.forClass(List.class);

		verify(rewardTimestampRepository).saveAll(rewardListCaptor.capture());

		List<RewardTimestamp> capturedRewardList = rewardListCaptor.getValue();

		assertThat(capturedRewardList).isEqualTo(expected);
	}
	
	@Test
	void shouldRedeemAt() {
		// given
		int userId = 1;
		String today = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));
		LocalDateTime todayDateTime = TimeUtils.ISOStringToTime(today);

		RewardTimestamp reward = new RewardTimestamp(null, userId, todayDateTime, null, todayDateTime.plusDays(1));
		
		BDDMockito.given(rewardTimestampRepository.getRewardByUserIdAt(ArgumentMatchers.any(),
				ArgumentMatchers.any())).willReturn(reward);

		// when
		RewardTimestamp expected = underTest.redeemAt(userId, today);

		// then
		ArgumentCaptor<RewardTimestamp> rewardCaptor = ArgumentCaptor.forClass(RewardTimestamp.class);

		verify(rewardTimestampRepository).save(rewardCaptor.capture());

		RewardTimestamp capturedReward = rewardCaptor.getValue();

		assertThat(capturedReward).isEqualTo(expected);
	}
	
	@Test
	void shouldNotRedeemAt_NoRewardFound() {
		// given
		int userId = 1;
		String today = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));

		BDDMockito.given(rewardTimestampRepository.getRewardByUserIdAt(ArgumentMatchers.any(),
				ArgumentMatchers.any())).willReturn(null);

		// when
		// then
		assertThatThrownBy(() -> underTest.redeemAt(userId, today))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("No reward available at " + today);
	}
	
	@Test
	void shouldNotRedeemAt_AlreadyRedeemed() {
		// given
		int userId = 1;
		String today = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));
		LocalDateTime todayDateTime = TimeUtils.ISOStringToTime(today);
		LocalDateTime now = LocalDateTime.now();

		RewardTimestamp reward = new RewardTimestamp(null, userId, todayDateTime, now, todayDateTime.plusDays(1));
		
		BDDMockito.given(rewardTimestampRepository.getRewardByUserIdAt(ArgumentMatchers.any(),
				ArgumentMatchers.any())).willReturn(reward);

		// when
		// then
		assertThatThrownBy(() -> underTest.redeemAt(userId, today))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("This reward has already been redeemed at " + TimeUtils.timeToISOString(reward.getRedeemedAt()));
	}
	
	@Test
	void shouldNotRedeemAt_NotAvailable() {
		// given
		int userId = 1;
		String today = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));
		LocalDateTime tomorrowDateTime = TimeUtils.ISOStringToTime(today).plusDays(1);

		RewardTimestamp reward = new RewardTimestamp(null, userId, tomorrowDateTime, null, tomorrowDateTime.plusDays(1));
		
		BDDMockito.given(rewardTimestampRepository.getRewardByUserIdAt(ArgumentMatchers.any(),
				ArgumentMatchers.any())).willReturn(reward);

		// when
		// then
		assertThatThrownBy(() -> underTest.redeemAt(userId, today))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("This reward is not available yet");
	}
	
	@Test
	void shouldNotRedeemAt_AlreadyExpired() {
		// given
		int userId = 1;
		String today = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));
		LocalDateTime yesterdayDateTime = TimeUtils.ISOStringToTime(today).minusDays(1);

		RewardTimestamp reward = new RewardTimestamp(null, userId, yesterdayDateTime, null, yesterdayDateTime.plusDays(1));
		
		BDDMockito.given(rewardTimestampRepository.getRewardByUserIdAt(ArgumentMatchers.any(),
				ArgumentMatchers.any())).willReturn(reward);

		// when
		// then
		assertThatThrownBy(() -> underTest.redeemAt(userId, today))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("This reward is already expired");
	}

}
