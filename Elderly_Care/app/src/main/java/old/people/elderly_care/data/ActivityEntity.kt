package old.people.elderly_care.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "elderly_id")
    val elderlyId: Int,

    @ColumnInfo(name = "caregiver_id")
    val caregiverId: Int,

    val title: String,
    val description: String,
    val date: String,
    val time: String,
    
    val type: String,
    val priority: String,
    
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false
)
