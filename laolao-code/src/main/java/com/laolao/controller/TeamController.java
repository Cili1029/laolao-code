package com.laolao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laolao.common.result.Result;
import com.laolao.pojo.dto.AdminTeamUpdateDTO;
import com.laolao.pojo.dto.CreateTeamDTO;
import com.laolao.pojo.dto.JoinTeamDTO;
import com.laolao.pojo.vo.*;
import com.laolao.service.TeamService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    @Resource
    private TeamService teamService;

    /**
     * 获取小组列表
     *
     * @return 小组列表
     */
    @GetMapping
    public Result<List<TeamVO>> getSimpleTeam() {
        return teamService.getSimpleTeam();
    }

    /**
     * 加入学习小组
     *
     * @param joinTeamDTO 加入小组请求参数
     * @return 操作结果
     */
    @PostMapping("/join")
    public Result<String> joinTeam(@RequestBody JoinTeamDTO joinTeamDTO) {
        return teamService.joinTeam(joinTeamDTO);
    }

    /**
     * 获取小组基础详情
     *
     * @param teamId 小组ID
     * @return 小组基础详情
     */
    @GetMapping("/detail-base")
    public Result<DetailBaseTeamVO> getDetailTeamBase(@RequestParam Integer teamId) {
        return teamService.getDetailTeamBase(teamId);
    }

    /**
     * 获取小组考试详情
     *
     * @param teamId 小组ID
     * @return 小组考试详情列表
     */
    @GetMapping("/detail-exam")
    public Result<List<DetailExamTeamVO>> getDetailTeamExam(@RequestParam Integer teamId) {
        return teamService.getDetailTeamExam(teamId);
    }

    /**
     * 导师创建学习小组
     *
     * @param createTeamDTO 创建小组请求参数
     * @return 操作结果
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public Result<String> createTeam(@RequestBody CreateTeamDTO createTeamDTO) {
        return teamService.createTeam(createTeamDTO);
    }

    /**
     * 获取所有小组
     *
     * @return 小组列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<AdminTeamVO>> getAllTeam(Integer pageNum, Integer pageSize, String content) {
        return teamService.getAllTeam(pageNum, pageSize, content);
    }

    /**
     * 启用/禁用组
     *
     * @param teamId 组Id
     * @return 结果信息
     */
    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> changeStatus(@RequestParam Integer teamId) {
        return teamService.changeStatus(teamId);
    }

    /**
     * 更新组信息
     *
     * @param adminTeamUpdateDTO 更新信息
     * @return 结果信息
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> update(@RequestBody AdminTeamUpdateDTO adminTeamUpdateDTO) {
        return teamService.updateUser(adminTeamUpdateDTO);
    }

    /**
     * 获取网站用户概览
     *
     * @return 用户概览
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AdminTeamSummaryVO> getSummary() {
        return teamService.getSummary();
    }
}
