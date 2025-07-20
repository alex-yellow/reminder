package com.example.reminder.dto;

import com.example.reminder.model.Reminder;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    Reminder toEntity(ReminderCreateDto dto);

    ReminderDto toDto(Reminder reminder);

    List<ReminderDto> toDtoList(List<Reminder> reminders);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ReminderUpdateDto dto, @MappingTarget Reminder entity);
}