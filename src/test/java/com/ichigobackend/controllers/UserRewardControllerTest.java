package com.ichigobackend.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ichigo.exceptions.CustomException;
import com.ichigobackend.dtos.GeneralResponse;
import com.ichigobackend.models.RewardTimestamp;
import com.ichigobackend.services.RewardService;
import com.ichigobackend.utils.TimeUtils;

@ExtendWith(MockitoExtension.class)
class UserRewardControllerTest {

	@Mock
	private RewardService rewardService;
	private UserRewardController underTest;

	@BeforeEach
	void setUp() throws Exception {
		underTest = new UserRewardController(rewardService);
	}

	@Test
	void shouldGetWeeklyRewardsAt() {
		// given
		String userId = "1";
		String now = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));

		LocalDateTime sun = LocalDateTime.of(2022, 4, 3, 0, 0, 0);
		List<RewardTimestamp> rewardList = new ArrayList<RewardTimestamp>();
		for (int i = 0; i < 7; i++) {
			RewardTimestamp reward = new RewardTimestamp(1, sun.plusDays(i));
			rewardList.add(reward);
		}

		BDDMockito.given(rewardService.getWeeklyRewardsAt(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.willReturn(rewardList);

		// when
		ResponseEntity res = underTest.getWeeklyRewardsAt(userId, now);
		// then
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldNotGetWeeklyRewardsAt_ThrowNumberFormatException() {
		// given
		String userId = "a";
		String now = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));

		// when
		ResponseEntity res = underTest.getWeeklyRewardsAt(userId, now);
		// then
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((GeneralResponse) res.getBody()).getError().getMessage())
				.isSubstringOf("NumberFormatException: userId must be an integer number");
	}

	@Test
	void shouldNotGetWeeklyRewardsAt_ThrowIllegalArgumentException() {
		// given
		String userId = "1";
		String timestampWrongFormat = "abc";

		BDDMockito.given(rewardService.getWeeklyRewardsAt(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.willThrow(IllegalArgumentException.class);

		// when
		ResponseEntity res = underTest.getWeeklyRewardsAt(userId, timestampWrongFormat);
		// then
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((GeneralResponse) res.getBody()).getError().getMessage()).isSubstringOf(
				"IllegalArgumentException: timestamp argument must be in ISO 8601 Extended format (YYYY-MM-DDTHH:mm:ss.ssZ)");
	}
	
	@Test
	void shouldRedeem() {
		// given
		String userId = "1";
		String now = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));

		BDDMockito.given(rewardService.redeemAt(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.willReturn(new RewardTimestamp(1, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)));

		// when
		ResponseEntity res = underTest.redeemRewardAt(userId, now);
		// then
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldNotRedeem_ThrowNumberFormatException() {
		// given
		String userId = "a";
		String now = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));

		// when
		ResponseEntity res = underTest.redeemRewardAt(userId, now);
		// then
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((GeneralResponse) res.getBody()).getError().getMessage())
				.isSubstringOf("NumberFormatException: userId must be an integer number");
	}

	@Test
	void shouldNotRedeem_ThrowIllegalArgumentException() {
		// given
		String userId = "1";
		String timestampWrongFormat = "abc";

		BDDMockito.given(rewardService.redeemAt(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.willThrow(IllegalArgumentException.class);

		// when
		ResponseEntity res = underTest.redeemRewardAt(userId, timestampWrongFormat);
		// then
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((GeneralResponse) res.getBody()).getError().getMessage()).isSubstringOf(
				"IllegalArgumentException: timestamp argument must be in ISO 8601 Extended format (YYYY-MM-DDTHH:mm:ss.ssZ)");
	}
	
	@Test
	void shouldNotRedeem_ThrowCustomException() {
		// given
		String userId = "1";
		String now = TimeUtils.timeToISOString(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));

		BDDMockito.given(rewardService.redeemAt(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.willThrow(CustomException.class);

		// when
		ResponseEntity res = underTest.redeemRewardAt(userId, now);
		// then
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

}
