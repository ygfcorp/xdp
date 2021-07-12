package com.kakao.xdp.flow.web.controller

import com.kakao.xdp.commons.server.infra.health.HealthSwitcher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

const val HEALTH_CHECK_PATH = "/health_check.html"

@RestController
class HealthCheckController(private val healthSwitcher: HealthSwitcher) {
    @RequestMapping(method = [RequestMethod.GET], path = [HEALTH_CHECK_PATH])
    fun deadOrAlive(): ResponseEntity<*> {
        return healthSwitcher.healthReport()
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/health/switch/{toBe}"])
    fun switchTo(@PathVariable(name = "toBe") toBe: HealthSwitcher.Switch) = healthSwitcher.switchTo(toBe)

    @RequestMapping(method = [RequestMethod.GET], path = ["/health/warmup"])
    fun warmup(@RequestParam(name = "accountId", required = true) accountId: Int) = ResponseEntity.ok().build<Any>()
}
