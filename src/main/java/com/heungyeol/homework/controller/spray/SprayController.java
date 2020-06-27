package com.heungyeol.homework.controller.spray;

import com.heungyeol.homework.service.spray.SprayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/spray")
public class SprayController {

    private final SprayService sprayService;

    @Autowired
    public SprayController(SprayService sprayService) {
        this.sprayService = sprayService;
    }

    @PutMapping(value = "/request")
    public String request(@RequestBody SprayDto dto
            , @RequestHeader(value = "X-USER-ID") String userId
            , @RequestHeader(value = "X-ROOM-ID") String roomId) {
        dto.setHeaderDto(userId, roomId);
        return sprayService.request(dto);
    }

    @PostMapping("/receive")
    public Long receive(@RequestBody TokenDto dto,
                          @RequestHeader(value = "X-USER-ID") String userId,
                          @RequestHeader(value = "X-ROOM-ID") String roomId) {
        dto.setHeaderDto(userId, roomId);
        return sprayService.receive(dto);
    }

    @PostMapping("/inquire")
    public ReturnDto inquire(@RequestBody TokenDto dto,
                          @RequestHeader(value = "X-USER-ID") String userId
            , @RequestHeader(value = "X-ROOM-ID") String roomId) {
        dto.setHeaderDto(userId, roomId);
        return sprayService.inquire(dto);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
