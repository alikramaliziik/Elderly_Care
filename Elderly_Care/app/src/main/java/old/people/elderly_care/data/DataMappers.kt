package old.people.elderly_care.data

import old.people.elderly_care.ActivityType
import old.people.elderly_care.Priority
import old.people.elderly_care.models.DailyActivity

fun ActivityEntity.toDomain(): DailyActivity {
    return DailyActivity(
        id = this.id,
        elderlyId = this.elderlyId,
        title = this.title,
        time = this.time,
        description = this.description,
        type = ActivityType.valueOf(this.type), 
        isCompleted = this.isCompleted,
        priority = Priority.valueOf(this.priority), 
        date = this.date,
        caregiverId = this.caregiverId
    )
}

fun DailyActivity.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = this.id,
        elderlyId = this.elderlyId,
        title = this.title,
        time = this.time,
        description = this.description,
        type = this.type.name,
        isCompleted = this.isCompleted,
        priority = this.priority.name,
        date = this.date,
        caregiverId = this.caregiverId
    )
}
