package com.ichigobackend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ichigo.exceptions.CustomException;
import com.ichigobackend.dtos.GeneralResponse;
import com.ichigobackend.dtos.RewardTimestampDto;
import com.ichigobackend.services.RewardService;

/**
 * 
 * @author huylq
 *
 */

@RestController
@RequestMapping("/users")
public class UserRewardController {

	private RewardService rewardService;

	public UserRewardController(RewardService rewardService) {
		this.rewardService = rewardService;
	}

	@GetMapping("/{userId}/rewards")
	public ResponseEntity<?> getWeeklyRewardsAt(@PathVariable("userId") String userId, @RequestParam("at") String at) {
		try {
			Integer userIdParsed = Integer.parseInt(userId);

			List<RewardTimestampDto> dtoList = rewardService.getWeeklyRewardsAt(userIdParsed, at).stream()
					.map((reward) -> RewardTimestampDto.toDto(reward)).collect(Collectors.toList());

			return ResponseEntity.ok(new GeneralResponse<List<RewardTimestampDto>>(dtoList));
		} catch (NumberFormatException e) {

			return new ResponseEntity<>(
					new GeneralResponse<>(
							new CustomException("NumberFormatException: userId must be an integer number")),
					HttpStatus.BAD_REQUEST);
		} catch (IllegalArgumentException e) {

			return new ResponseEntity<>(new GeneralResponse<>(new CustomException(
					"IllegalArgumentException: timestamp argument must be in ISO 8601 Extended format (YYYY-MM-DDTHH:mm:ss.ssZ)")),
					HttpStatus.BAD_REQUEST);
		}
	}

	@PatchMapping("/{userId}/rewards/{availableAt}/redeem")
	public ResponseEntity<?> redeemRewardAt(@PathVariable("userId") String userId,
			@PathVariable("availableAt") String availableAt) {
		try {
			Integer userIdParsed = Integer.parseInt(userId);

			RewardTimestampDto dto = RewardTimestampDto.toDto(rewardService.redeemAt(userIdParsed, availableAt));

			return ResponseEntity.ok(dto);
		} catch (NumberFormatException e) {

			return new ResponseEntity<>(
					new GeneralResponse<>(
							new CustomException("NumberFormatException: userId must be an integer number")),
					HttpStatus.BAD_REQUEST);
		} catch (IllegalArgumentException e) {

			return new ResponseEntity<>(new GeneralResponse<>(new CustomException(
					"IllegalArgumentException: timestamp argument must be in ISO 8601 Extended format (YYYY-MM-DDTHH:mm:ss.ssZ)")),
					HttpStatus.BAD_REQUEST);
		} catch (CustomException e) {

			return new ResponseEntity<>(new GeneralResponse<>(e), HttpStatus.BAD_REQUEST);
		}
	}

}
