package clofi.runningplanet.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import clofi.runningplanet.mission.service.MissionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MissionScheduler {

	private final MissionService missionService;

	@Scheduled(cron = "0 0 5 * * *")
	public void dailyMissionSchedule() {
		missionService.createDailyMission();
	}
}
