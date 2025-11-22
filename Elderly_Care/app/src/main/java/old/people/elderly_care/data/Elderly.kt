package old.people.elderly_care.data

import androidx.room.*

@Entity(
    tableName = "elderly",
    foreignKeys = [ForeignKey(
        entity = Caregiver::class,
        parentColumns = ["id"],
        childColumns = ["caregiver_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Elderly(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "caregiver_id")
    val caregiverId: Int,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    val age: Int,
    val gender: String,
    val phone: String?
)
