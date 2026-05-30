package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Chronic
import com.example.data.model.Echo
import com.example.data.repository.BabuuuuRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class BabuuuuTab {
    HOME, SEARCH, CREATE, MESSAGES, NOTIFICATIONS, PROFILE
}

class BabuuuuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BabuuuuRepository
    val allChronicles: StateFlow<List<Chronic>>
    
    // UI states
    private val _currentTab = MutableStateFlow(BabuuuuTab.HOME)
    val currentTab: StateFlow<BabuuuuTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedChronicIdForComments = MutableStateFlow<Int?>(null)
    val selectedChronicIdForComments: StateFlow<Int?> = _selectedChronicIdForComments.asStateFlow()

    val currentEchoes: StateFlow<List<Echo>> = _selectedChronicIdForComments
        .flatMapLatest { id ->
            if (id != null) {
                repository.getEchoesForChronic(id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Futuristic notifications simulated log
    private val _notifications = MutableStateFlow<List<SystemAlert>>(emptyList())
    val notifications: StateFlow<List<SystemAlert>> = _notifications.asStateFlow()

    // Futuristic chat simulated contacts
    private val _activeChatContact = MutableStateFlow<ChatContact?>(null)
    val activeChatContact: StateFlow<ChatContact?> = _activeChatContact.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val dao = database.babuuuuDao()
        repository = BabuuuuRepository(dao)

        allChronicles = repository.allChronicles
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Seed data if empty
        viewModelScope.launch {
            allChronicles.first { it.isNotEmpty() || true } // Wait for flow or initialize
            val count = dao.getAllChronicles().first().size
            if (count == 0) {
                seedInitialChronicles()
            }
            seedSimulatedNotifications()
        }
    }

    private suspend fun seedInitialChronicles() {
        val initials = listOf(
            Chronic(
                username = "Valkyrie_Zero",
                userHandle = "valk.00",
                avatarColorIndex = 1,
                timestamp = System.currentTimeMillis() - 600000,
                latency = "0.94ms",
                nodeSector = "ORBITAL-STATION-7",
                text = "Holographic telemetry shields stabilized. Deployed standard scanning routine over sector Cygnus-B. Quantum fluctuations detected at sub-atomic levels. Monitoring continues...",
                pulseCount = 42,
                commentsCount = 3,
                visualSeed = 101
            ),
            Chronic(
                username = "Aether_Architect",
                userHandle = "aether.core",
                avatarColorIndex = 2,
                timestamp = System.currentTimeMillis() - 3600000,
                latency = "2.41ms",
                nodeSector = "VOID-SEC-40",
                text = "Babuuuu protocol upgrade initialized successfully. Code compiles in 11 dimensions. Transmitting the new obsidian grid canvas interface across all sub-servers. Welcome to next-gen social syncing.",
                pulseCount = 88,
                commentsCount = 2,
                visualSeed = 202
            ),
            Chronic(
                username = "Stellar_Cartographer",
                userHandle = "star.mapper",
                avatarColorIndex = 3,
                timestamp = System.currentTimeMillis() - 7200000,
                latency = "4.12ms",
                nodeSector = "NEBULA-HUB-Z",
                text = "Solar winds peaking at 450 km/s. Cosmic background radiation is writing poetry onto our database sectors. Recommend local shield amplification. Beautiful auroras captured in outer sensors.",
                pulseCount = 105,
                commentsCount = 0,
                visualSeed = 303
            )
        )

        initials.forEach { repository.insertChronic(it) }

        // Seed some initial Echo comments
        repository.addComment(Echo(chronicId = 1, username = "Cyber_Neophyte", userHandle = "neophyte.9", text = "Is the sub-atomic fluctuation stable or expanding?", timestamp = System.currentTimeMillis() - 500000))
        repository.addComment(Echo(chronicId = 1, username = "Quantum_Spectre", userHandle = "spectre.q", text = "Scan sector 4 as well, we got telemetry spikes there.", timestamp = System.currentTimeMillis() - 400000))
        repository.addComment(Echo(chronicId = 1, username = "Valkyrie_Zero", userHandle = "valk.00", text = "Verified. Expanding slightly but shield holds.", timestamp = System.currentTimeMillis() - 300000))

        repository.addComment(Echo(chronicId = 2, username = "Grid_Runner", userHandle = "runner.grid", text = "UI looks absolutely crisp! Loving the Neon cyan neon glow overlays.", timestamp = System.currentTimeMillis() - 2200000))
        repository.addComment(Echo(chronicId = 2, username = "Aether_Architect", userHandle = "aether.core", text = "Appreciate the feedback, telemetry streams are standard.", timestamp = System.currentTimeMillis() - 2000000))
    }

    private fun seedSimulatedNotifications() {
        _notifications.value = listOf(
            SystemAlert("NEO-BURST", "Solar prominence flare recorded. Transmitted data packets shielded.", "ALERT-LEVEL-MEDIUM", System.currentTimeMillis() - 120000),
            SystemAlert("CORE-SYNC", "Your local node was optimized. Bandwidth throttle increased to 40 GB/s.", "STATUS-OPTIMAL", System.currentTimeMillis() - 900000),
            SystemAlert("SIGNAL-ECHO", "@astro_physicist echoed on your transmission coordinates.", "TRANSMISSION", System.currentTimeMillis() - 1800000),
            SystemAlert("PULSE-WAVE", "Chronicle 'Holographic shields online' gained 15 quantum pulses globally.", "PULSE-WAVE", System.currentTimeMillis() - 3600000)
        )
    }

    fun setTab(tab: BabuuuuTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectChronicForComments(chronicId: Int?) {
        _selectedChronicIdForComments.value = chronicId
    }

    fun togglePulse(chronic: Chronic) {
        viewModelScope.launch {
            val updated = chronic.copy(
                isPulsed = !chronic.isPulsed,
                pulseCount = if (chronic.isPulsed) chronic.pulseCount - 1 else chronic.pulseCount + 1
            )
            repository.updateChronic(updated)
        }
    }

    fun toggleArchive(chronic: Chronic) {
        viewModelScope.launch {
            val updated = chronic.copy(isArchived = !chronic.isArchived)
            repository.updateChronic(updated)
        }
    }

    fun addComment(chronicId: Int, opinionText: String) {
        if (opinionText.isBlank()) return
        viewModelScope.launch {
            val newEcho = Echo(
                chronicId = chronicId,
                username = "Pilot_User_Guest",
                userHandle = "guest.pilot",
                text = opinionText,
                timestamp = System.currentTimeMillis(),
                sectorSource = "LOCAL-TERMINAL-01"
            )
            repository.addComment(newEcho)
        }
    }

    fun addChronic(content: String, sector: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val customSector = if (sector.isNotBlank()) sector.uppercase() else "GRID-TERMINAL-1"
            val newChronic = Chronic(
                username = "Pilot_User_Guest",
                userHandle = "guest.pilot",
                avatarColorIndex = 0,
                timestamp = System.currentTimeMillis(),
                latency = "${(10..99).random() / 10.0}ms",
                nodeSector = customSector,
                text = content,
                pulseCount = 0,
                commentsCount = 0,
                visualSeed = (100..999).random()
            )
            repository.insertChronic(newChronic)
            _currentTab.value = BabuuuuTab.HOME // return to feed
        }
    }

    // Chat simulated interactions
    fun startChatWith(contact: ChatContact) {
        _activeChatContact.value = contact
        _chatMessages.value = listOf(
            ChatMessage(contact.username, contact.initialMessage, false, System.currentTimeMillis() - 600000)
        )
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val currentContact = _activeChatContact.value ?: return
        val guestMsg = ChatMessage("guest.pilot", text, true, System.currentTimeMillis())
        _chatMessages.value = _chatMessages.value + guestMsg

        // Cybermatic responsive robot reply
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            val responseText = when {
                text.contains("hello", true) || text.contains("hi", true) -> "Secure handshake confirmed. Node connection stable."
                text.contains("pulse", true) -> "Pulse beam received. Amplifying quantum resonators."
                text.contains("help", true) -> "Terminal support diagnostic: ensure coordinates are valid, transmit with standard parity."
                else -> "Message routed via subspace relay. Latency logged at 1.48ms."
            }
            val robotMsg = ChatMessage(currentContact.username, responseText, false, System.currentTimeMillis())
            _chatMessages.value = _chatMessages.value + robotMsg
        }
    }

    fun clearActiveChat() {
        _activeChatContact.value = null
        _chatMessages.value = emptyList()
    }
}

// Model helpers for high-fidelity features
data class SystemAlert(
    val type: String,
    val description: String,
    val category: String,
    val time: Long
)

data class ChatContact(
    val username: String,
    val handle: String,
    val sector: String,
    val status: String,
    val avatarColorIndex: Int,
    val initialMessage: String
)

data class ChatMessage(
    val sender: String,
    val content: String,
    val isMe: Boolean,
    val timestamp: Long
)

class BabuuuuViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BabuuuuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BabuuuuViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
