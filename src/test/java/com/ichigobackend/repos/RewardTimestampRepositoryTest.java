/**
 * 
 */
package com.ichigobackend.repos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ichigobackend.models.RewardTimestamp;

/**
 * @author huylq
 *
 */

@DataJpaTest
class RewardTimestampRepositoryTest {
	
	@Autowired
	private RewardTimestampRepository underTest;

	@AfterEach
	void tearDown() {
		underTest.deleteAll();
	}

	@Test
	void shouldGetWeeklyRewards() {
		//given
		int userId = 1;

		LocalDateTime sun = LocalDateTime.of(2022, 4, 3, 0, 0, 0);
		LocalDateTime mon = LocalDateTime.of(2022, 4, 4, 0, 0, 0);
		LocalDateTime tue = LocalDateTime.of(2022, 4, 5, 0, 0, 0);
		LocalDateTime wed = LocalDateTime.of(2022, 4, 6, 0, 0, 0);
		LocalDateTime thu = LocalDateTime.of(2022, 4, 7, 0, 0, 0);
		LocalDateTime fri = LocalDateTime.of(2022, 4, 8, 0, 0, 0);
		LocalDateTime sat = LocalDateTime.of(2022, 4, 9, 0, 0, 0);
		
		List<RewardTimestamp> rewardList = new ArrayList<RewardTimestamp>();
		rewardList.add(new RewardTimestamp(userId, sun));
		rewardList.add(new RewardTimestamp(userId, mon));
		rewardList.add(new RewardTimestamp(userId, tue));
		rewardList.add(new RewardTimestamp(userId, wed));
		rewardList.add(new RewardTimestamp(userId, thu));
		rewardList.add(new RewardTimestamp(userId, fri));
		rewardList.add(new RewardTimestamp(userId, sat));
		
		underTest.saveAll(rewardList);
		
		//when
		List<RewardTimestamp> expected = underTest.getWeeklyRewardsByUserIdAndStartEnd(userId, sun, sat);
		
		//then
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
	void shouldNotGetWeeklyRewards() {
		//given
		int userId = 1;
		LocalDateTime sun = LocalDateTime.of(2022, 4, 3, 0, 0, 0);
		LocalDateTime sat = LocalDateTime.of(2022, 4, 9, 0, 0, 0);
		
		//when
		List<RewardTimestamp> expected = underTest.getWeeklyRewardsByUserIdAndStartEnd(userId, sun, sat);
		
		//then
		assertThat(expected.size()).isEqualTo(0);
		
	}
	
	@Test
	void shouldGetRewardAt() {
		//given
		int userId = 1;
		LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

		underTest.save(new RewardTimestamp(userId, now));
		
		//when
		RewardTimestamp expected = underTest.getRewardByUserIdAt(userId, now);
		
		//then
		assertThat(expected.getAvailableAt()).isEqualTo(now);
	}
	
	@Test
	void shouldNotGetRewardAt() {
		//given
		int userId = 1;
		LocalDateTime now = LocalDateTime.now().withHour(0);
		
		//when
		RewardTimestamp expected = underTest.getRewardByUserIdAt(userId, now);
		
		//then
		assertThat(expected).isNull();
	}

}
