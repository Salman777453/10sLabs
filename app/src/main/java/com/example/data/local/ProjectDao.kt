package com.example.data.local

import androidx.room.*
import com.example.data.model.AudioProject
import com.example.data.model.ClonedVoice
import com.example.data.model.PronunciationRule
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<AudioProject>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): AudioProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: AudioProject)

    @Update
    suspend fun updateProject(project: AudioProject)

    @Delete
    suspend fun deleteProject(project: AudioProject)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)
}

@Dao
interface PronunciationDao {
    @Query("SELECT * FROM pronunciation_rules ORDER BY wordToReplace ASC")
    fun getAllRules(): Flow<List<PronunciationRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: PronunciationRule)

    @Update
    suspend fun updateRule(rule: PronunciationRule)

    @Delete
    suspend fun deleteRule(rule: PronunciationRule)

    @Query("DELETE FROM pronunciation_rules WHERE id = :id")
    suspend fun deleteRuleById(id: String)
}

@Dao
interface ClonedVoiceDao {
    @Query("SELECT * FROM cloned_voices ORDER BY createdAt DESC")
    fun getAllClones(): Flow<List<ClonedVoice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClone(clone: ClonedVoice)

    @Delete
    suspend fun deleteClone(clone: ClonedVoice)

    @Query("DELETE FROM cloned_voices WHERE id = :id")
    suspend fun deleteCloneById(id: String)
}
