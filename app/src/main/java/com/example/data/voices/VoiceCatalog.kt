package com.example.data.voices

import com.example.data.model.VoiceProfile

object VoiceCatalog {

    val allVoices: List<VoiceProfile> by lazy {
        val list = mutableListOf<VoiceProfile>()

        // 1. NARRATION & DOCUMENTARY (25 voices)
        list.addAll(
            listOf(
                VoiceProfile("doc_01", "David Sterling", "Male", "Mature", "UK", "en-GB", "Narration", "Deep, authoritative BBC-style nature documentary narrator", pitchMultiplier = 0.88f, speedMultiplier = 0.95f),
                VoiceProfile("doc_02", "Eleanor Vance", "Female", "Adult", "UK", "en-GB", "Narration", "Prestigious historical chronicle narrator with crisp diction", pitchMultiplier = 1.0f, speedMultiplier = 0.95f),
                VoiceProfile("doc_03", "Morgan Hayes", "Male", "Elder", "US", "en-US", "Narration", "Resonant cinematic narrator reminiscent of legendary astronomy docs", pitchMultiplier = 0.82f, speedMultiplier = 0.92f),
                VoiceProfile("doc_04", "Clara Beauchamp", "Female", "Mature", "US", "en-US", "Narration", "Warm, cultured museum curator and science storyteller", pitchMultiplier = 0.98f, speedMultiplier = 0.96f),
                VoiceProfile("doc_05", "Alistair Finch", "Male", "Elder", "UK", "en-GB", "Narration", "Aged explorer recounting lost expeditions in the Arctic", pitchMultiplier = 0.85f, speedMultiplier = 0.90f),
                VoiceProfile("doc_06", "Beatrice Montgomery", "Female", "Adult", "Canada", "en-CA", "Narration", "Clear, engaging wildlife biologist voice with natural cadence", pitchMultiplier = 1.02f, speedMultiplier = 1.0f),
                VoiceProfile("doc_07", "Julian Mercer", "Male", "Adult", "Australia", "en-AU", "Narration", "Rugged outdoor adventurer with rich baritone timbre", pitchMultiplier = 0.90f, speedMultiplier = 0.98f),
                VoiceProfile("doc_08", "Fiona MacLeod", "Female", "Mature", "Scotland", "en-GB", "Narration", "Poetic Highlands chronicler with a lyrical cadence", pitchMultiplier = 1.05f, speedMultiplier = 0.94f),
                VoiceProfile("doc_09", "Caleb Thorne", "Male", "Adult", "US", "en-US", "Narration", "True-crime documentary investigative narrator with subtle tension", pitchMultiplier = 0.87f, speedMultiplier = 0.93f),
                VoiceProfile("doc_10", "Hannah Lindqvist", "Female", "Adult", "Global", "en-US", "Narration", "Modern European architectural and design narrator", pitchMultiplier = 1.02f, speedMultiplier = 1.0f),
                VoiceProfile("doc_11", "Dr. Victor Vance", "Male", "Elder", "US", "en-US", "Narration", "Academic historian lecturing on antiquity and empires", pitchMultiplier = 0.84f, speedMultiplier = 0.91f),
                VoiceProfile("doc_12", "Sylvia Rhodes", "Female", "Elder", "UK", "en-GB", "Narration", "Aristocratic biographer with refined, deliberate pauses", pitchMultiplier = 0.94f, speedMultiplier = 0.89f),
                VoiceProfile("doc_13", "Rajiv Menon", "Male", "Adult", "India", "en-IN", "Narration", "Cosmopolitan scientific narrator with articulate resonance", pitchMultiplier = 0.95f, speedMultiplier = 0.98f),
                VoiceProfile("doc_14", "Ananya Sharma", "Female", "Adult", "India", "en-IN", "Narration", "Warm documentary storyteller exploring cultural heritage", pitchMultiplier = 1.06f, speedMultiplier = 0.97f),
                VoiceProfile("doc_15", "Declan O'Connor", "Male", "Adult", "Ireland", "en-IE", "Narration", "Expressive folklore narrator with rhythmic Irish warmth", pitchMultiplier = 0.92f, speedMultiplier = 0.96f),
                VoiceProfile("doc_16", "Siobhan Kelly", "Female", "Adult", "Ireland", "en-IE", "Narration", "Gentle lyrical storyteller with soothing Celtic tone", pitchMultiplier = 1.04f, speedMultiplier = 0.93f),
                VoiceProfile("doc_17", "Garrison Cole", "Male", "Mature", "US", "en-US", "Narration", "Gravelly deep-sea documentary narrator", pitchMultiplier = 0.78f, speedMultiplier = 0.90f),
                VoiceProfile("doc_18", "Miriam Al-Hassan", "Female", "Adult", "Global", "en-US", "Narration", "International affairs and geopolitical analyst voice", pitchMultiplier = 1.01f, speedMultiplier = 1.0f),
                VoiceProfile("doc_19", "Hendrik van Dijk", "Male", "Adult", "Global", "en-US", "Narration", "Thoughtful philosophical essay narrator", pitchMultiplier = 0.91f, speedMultiplier = 0.92f),
                VoiceProfile("doc_20", "Therese Moreau", "Female", "Adult", "Global", "en-US", "Narration", "Subtle Parisian elegance narrating art and culinary docs", pitchMultiplier = 1.03f, speedMultiplier = 0.95f),
                VoiceProfile("doc_21", "Wesley Brooks", "Male", "Adult", "US", "en-US", "Narration", "Aviation and aerospace documentary narrator", pitchMultiplier = 0.93f, speedMultiplier = 1.02f),
                VoiceProfile("doc_22", "Audrey Prescott", "Female", "Adult", "UK", "en-GB", "Narration", "Literary fiction audiobook specialist with velvet warmth", pitchMultiplier = 1.0f, speedMultiplier = 0.94f),
                VoiceProfile("doc_23", "Bartholomew Kane", "Male", "Elder", "US", "en-US", "Narration", "Epic cinematic fantasy prologue narrator", pitchMultiplier = 0.80f, speedMultiplier = 0.88f),
                VoiceProfile("doc_24", "Genevieve Fontaine", "Female", "Mature", "Canada", "en-CA", "Narration", "Historical biography reader with nuanced gravitas", pitchMultiplier = 0.96f, speedMultiplier = 0.93f),
                VoiceProfile("doc_25", "Malcolm Reed", "Male", "Mature", "UK", "en-GB", "Narration", "Scientific exploration narrator with classic BBC clarity", pitchMultiplier = 0.89f, speedMultiplier = 0.97f)
            )
        )

        // 2. PODCAST HOSTS & CONVERSATIONAL (25 voices)
        list.addAll(
            listOf(
                VoiceProfile("pod_01", "Leo Carter", "Male", "Adult", "US", "en-US", "Podcast", "Casual, engaging tech podcaster with a relatable smile", pitchMultiplier = 1.02f, speedMultiplier = 1.05f),
                VoiceProfile("pod_02", "Maya Lin", "Female", "Adult", "US", "en-US", "Podcast", "Thoughtful conversationalist and lifestyle interview host", pitchMultiplier = 1.08f, speedMultiplier = 1.02f),
                VoiceProfile("pod_03", "Samir Patel", "Male", "Adult", "US", "en-US", "Podcast", "Fast-paced startup founder and tech commentator", pitchMultiplier = 1.05f, speedMultiplier = 1.10f),
                VoiceProfile("pod_04", "Chloe Bennett", "Female", "Youth", "US", "en-US", "Podcast", "Energetic pop culture podcaster with natural laugh inflections", pitchMultiplier = 1.14f, speedMultiplier = 1.08f),
                VoiceProfile("pod_05", "Arthur Pendelton", "Male", "Mature", "UK", "en-GB", "Podcast", "Witty, satirical British political talk host", pitchMultiplier = 0.94f, speedMultiplier = 1.04f),
                VoiceProfile("pod_06", "Zara Hadid", "Female", "Adult", "UK", "en-GB", "Podcast", "Smart, candid cultural critic and interview host", pitchMultiplier = 1.03f, speedMultiplier = 1.02f),
                VoiceProfile("pod_07", "Toby Higgins", "Male", "Adult", "Australia", "en-AU", "Podcast", "Laid-back Aussie comedy and sports podcast co-host", pitchMultiplier = 0.98f, speedMultiplier = 1.06f),
                VoiceProfile("pod_08", "Kylie Campbell", "Female", "Adult", "Australia", "en-AU", "Podcast", "Upbeat travel and adventure story host", pitchMultiplier = 1.10f, speedMultiplier = 1.05f),
                VoiceProfile("pod_09", "Arjun Kapoor", "Male", "Adult", "India", "en-IN", "Podcast", "Charismatic business insider and conversation leader", pitchMultiplier = 1.0f, speedMultiplier = 1.06f),
                VoiceProfile("pod_10", "Priya Nair", "Female", "Adult", "India", "en-IN", "Podcast", "Intimate, warm self-improvement and wellness podcast host", pitchMultiplier = 1.07f, speedMultiplier = 0.98f),
                VoiceProfile("pod_11", "Marcus Brody", "Male", "Adult", "US", "en-US", "Podcast", "Deep, calm late-night radio call-in host", pitchMultiplier = 0.86f, speedMultiplier = 0.93f),
                VoiceProfile("pod_12", "Evelyn Cross", "Female", "Adult", "US", "en-US", "Podcast", "True crime investigative journalist podcast host", pitchMultiplier = 0.99f, speedMultiplier = 0.97f),
                VoiceProfile("pod_13", "Finnian Walsh", "Male", "Adult", "Ireland", "en-IE", "Podcast", "Humorous pub-banter comedy storyteller", pitchMultiplier = 1.02f, speedMultiplier = 1.07f),
                VoiceProfile("pod_14", "Brooke Sterling", "Female", "Adult", "US", "en-US", "Podcast", "High-energy morning talk show host", pitchMultiplier = 1.12f, speedMultiplier = 1.12f),
                VoiceProfile("pod_15", "Dante Rossi", "Male", "Adult", "Global", "en-US", "Podcast", "Charismatic food and lifestyle creator", pitchMultiplier = 0.96f, speedMultiplier = 1.04f),
                VoiceProfile("pod_16", "Yuki Tanaka", "Female", "Adult", "Global", "en-US", "Podcast", "Insightful global tech trend reporter", pitchMultiplier = 1.09f, speedMultiplier = 1.02f),
                VoiceProfile("pod_17", "Gabe Martinez", "Male", "Adult", "US", "en-US", "Podcast", "Authentic sports analyst with passionate delivery", pitchMultiplier = 0.97f, speedMultiplier = 1.08f),
                VoiceProfile("pod_18", "Sierra Gomez", "Female", "Adult", "US", "en-US", "Podcast", "Bilingual storytelling and social issues podcast host", pitchMultiplier = 1.05f, speedMultiplier = 1.03f),
                VoiceProfile("pod_19", "Liam Fletcher", "Male", "Adult", "Canada", "en-CA", "Podcast", "Cozy conversationalist with genuine listener empathy", pitchMultiplier = 0.95f, speedMultiplier = 0.99f),
                VoiceProfile("pod_20", "Zoe Kravitz", "Female", "Adult", "US", "en-US", "Podcast", "Soulful, relaxed evening conversation host", pitchMultiplier = 0.95f, speedMultiplier = 0.94f),
                VoiceProfile("pod_21", "Nico Becker", "Male", "Adult", "Global", "en-US", "Podcast", "Electronic music and nightlife scene host", pitchMultiplier = 0.94f, speedMultiplier = 1.05f),
                VoiceProfile("pod_22", "Talia Bloom", "Female", "Youth", "US", "en-US", "Podcast", "Gen-Z viral trends and social commentary host", pitchMultiplier = 1.16f, speedMultiplier = 1.11f),
                VoiceProfile("pod_23", "Oliver Vance", "Male", "Mature", "UK", "en-GB", "Podcast", "Cerebral philosophy and book review podcast host", pitchMultiplier = 0.91f, speedMultiplier = 0.93f),
                VoiceProfile("pod_24", "Ruby Chen", "Female", "Adult", "US", "en-US", "Podcast", "Creative writer and narrative podcast host", pitchMultiplier = 1.06f, speedMultiplier = 0.99f),
                VoiceProfile("pod_25", "Zachary King", "Male", "Adult", "US", "en-US", "Podcast", "Gaming streamer and unboxing video voice", pitchMultiplier = 1.08f, speedMultiplier = 1.13f)
            )
        )

        // 3. DEEP BASS & AUTHORITATIVE (20 voices)
        list.addAll(
            listOf(
                VoiceProfile("bass_01", "Titan Vance", "Male", "Mature", "US", "en-US", "Deep Bass", "Subterranean ultra-deep bass for trailers and epic announcements", pitchMultiplier = 0.70f, speedMultiplier = 0.90f),
                VoiceProfile("bass_02", "Goliath Drake", "Male", "Elder", "UK", "en-GB", "Deep Bass", "Heavy resonant bass with Shakespearean theatrical authority", pitchMultiplier = 0.73f, speedMultiplier = 0.88f),
                VoiceProfile("bass_03", "Commander Vance", "Male", "Mature", "US", "en-US", "Deep Bass", "Military tactical commander issuing orbital defense orders", pitchMultiplier = 0.75f, speedMultiplier = 0.95f),
                VoiceProfile("bass_04", "Brimstone Morgan", "Male", "Elder", "US", "en-US", "Deep Bass", "Gravelly outlaw elder of the frontier canyon", pitchMultiplier = 0.72f, speedMultiplier = 0.87f),
                VoiceProfile("bass_05", "Father Gregory", "Male", "Elder", "Global", "en-US", "Deep Bass", "Solemn cathedral resonant bass with ecclesiastical gravity", pitchMultiplier = 0.74f, speedMultiplier = 0.86f),
                VoiceProfile("bass_06", "Victor Kane", "Male", "Mature", "US", "en-US", "Deep Bass", "Corporate CEO delivering quarterly global dominance briefing", pitchMultiplier = 0.79f, speedMultiplier = 0.98f),
                VoiceProfile("bass_07", "Dmitri Volkov", "Male", "Mature", "Global", "en-US", "Deep Bass", "Deep Slavic-accented heavy operative voice", pitchMultiplier = 0.76f, speedMultiplier = 0.92f),
                VoiceProfile("bass_08", "Kaelen Grim", "Male", "Mature", "Scotland", "en-GB", "Deep Bass", "Highland clan chieftain with roaring low-end presence", pitchMultiplier = 0.78f, speedMultiplier = 0.94f),
                VoiceProfile("bass_09", "The Judge", "Male", "Elder", "US", "en-US", "Deep Bass", "Unforgiving judiciary magistrate pronouncing the verdict", pitchMultiplier = 0.71f, speedMultiplier = 0.85f),
                VoiceProfile("bass_10", "Ares Thorne", "Male", "Adult", "US", "en-US", "Deep Bass", "Heavyweight boxing champion announcer and hype beast", pitchMultiplier = 0.80f, speedMultiplier = 1.05f),
                VoiceProfile("bass_11", "Ironhide Briggs", "Male", "Mature", "Australia", "en-AU", "Deep Bass", "Rugged Outback roadmaster with weathered bass vocals", pitchMultiplier = 0.77f, speedMultiplier = 0.93f),
                VoiceProfile("bass_12", "Archon Prime", "Male", "Mature", "Global", "en-US", "Deep Bass", "Cosmic entity booming from the galactic core", pitchMultiplier = 0.68f, speedMultiplier = 0.84f),
                VoiceProfile("bass_13", "Baron Von Croft", "Male", "Mature", "UK", "en-GB", "Deep Bass", "Aristocratic gothic vampire lord speaking from shadow", pitchMultiplier = 0.76f, speedMultiplier = 0.89f),
                VoiceProfile("bass_14", "Chief Redford", "Male", "Elder", "US", "en-US", "Deep Bass", "Wise mountain tribal elder imparting ancient prophecies", pitchMultiplier = 0.73f, speedMultiplier = 0.86f),
                VoiceProfile("bass_15", "Brock Sterling", "Male", "Adult", "US", "en-US", "Deep Bass", "Fitness powerhouse delivering motivational morning commands", pitchMultiplier = 0.81f, speedMultiplier = 1.02f),
                VoiceProfile("bass_16", "Malachi Vance", "Male", "Mature", "US", "en-US", "Deep Bass", "Southern gothic preacher with dark resonant vibrato", pitchMultiplier = 0.74f, speedMultiplier = 0.91f),
                VoiceProfile("bass_17", "Gunnar Stone", "Male", "Adult", "Global", "en-US", "Deep Bass", "Nordic blacksmith warrior with booming cadence", pitchMultiplier = 0.75f, speedMultiplier = 0.90f),
                VoiceProfile("bass_18", "Magnus Bell", "Male", "Elder", "UK", "en-GB", "Deep Bass", "Victorian industrialist baron commanding the iron mills", pitchMultiplier = 0.76f, speedMultiplier = 0.92f),
                VoiceProfile("bass_19", "Shadow Broker", "Male", "Mature", "Global", "en-US", "Deep Bass", "Mysterious syndicate syndicate leader whispering in low register", pitchMultiplier = 0.72f, speedMultiplier = 0.91f),
                VoiceProfile("bass_20", "Orion Dread", "Male", "Adult", "US", "en-US", "Deep Bass", "Dark fantasy overlord anticipating the final eclipse", pitchMultiplier = 0.69f, speedMultiplier = 0.86f)
            )
        )

        // 4. SOFT, WHISPER & MEDITATION (20 voices)
        list.addAll(
            listOf(
                VoiceProfile("whisper_01", "Aria Moon", "Female", "Adult", "US", "en-US", "Meditation", "Ethereal guided sleep meditation and hypnotherapy guide", pitchMultiplier = 1.02f, speedMultiplier = 0.78f),
                VoiceProfile("whisper_02", "Solomon Peace", "Male", "Mature", "UK", "en-GB", "Meditation", "Gentle mindfulness coach guiding breathwork and presence", pitchMultiplier = 0.89f, speedMultiplier = 0.80f),
                VoiceProfile("whisper_03", "Seraphina Dawn", "Female", "Adult", "Ireland", "en-IE", "Soft Whisper", "ASMR whispering storyteller reading cozy bedtime fairy tales", pitchMultiplier = 1.08f, speedMultiplier = 0.82f),
                VoiceProfile("whisper_04", "River Woods", "Female", "Adult", "US", "en-US", "Meditation", "Zen garden breathing guide with soft melodic cadence", pitchMultiplier = 1.04f, speedMultiplier = 0.79f),
                VoiceProfile("whisper_05", "Bodhi Thorne", "Male", "Adult", "US", "en-US", "Meditation", "Tibetan sound bowl accompanist and grounding meditation leader", pitchMultiplier = 0.93f, speedMultiplier = 0.82f),
                VoiceProfile("whisper_06", "Luna Del Mar", "Female", "Adult", "Global", "en-US", "Soft Whisper", "Velvet whisper reading nocturnal Spanish-influenced lullabies", pitchMultiplier = 1.06f, speedMultiplier = 0.80f),
                VoiceProfile("whisper_07", "Kiran Verma", "Male", "Adult", "India", "en-IN", "Meditation", "Serene yoga master leading body scan relaxation", pitchMultiplier = 0.96f, speedMultiplier = 0.81f),
                VoiceProfile("whisper_08", "Isla Heather", "Female", "Youth", "Scotland", "en-GB", "Soft Whisper", "Gentle Highlands whisperer reciting nocturnal poetry", pitchMultiplier = 1.11f, speedMultiplier = 0.84f),
                VoiceProfile("whisper_09", "Caspian Frost", "Male", "Adult", "UK", "en-GB", "Soft Whisper", "Intimate late-night book reader by a crackling hearth", pitchMultiplier = 0.92f, speedMultiplier = 0.83f),
                VoiceProfile("whisper_10", "Anahi Silva", "Female", "Adult", "Global", "en-US", "Meditation", "Reiki practitioner offering affirmations of peace and renewal", pitchMultiplier = 1.05f, speedMultiplier = 0.80f),
                VoiceProfile("whisper_11", "Julian Whisper", "Male", "Adult", "US", "en-US", "Soft Whisper", "Soft-spoken audio companion for insomnia relief", pitchMultiplier = 0.90f, speedMultiplier = 0.81f),
                VoiceProfile("whisper_12", "Celeste Ray", "Female", "Adult", "Canada", "en-CA", "Meditation", "Starlight visualization guide for lucid dreaming", pitchMultiplier = 1.07f, speedMultiplier = 0.78f),
                VoiceProfile("whisper_13", "Mira Sol", "Female", "Mature", "Global", "en-US", "Meditation", "Comforting motherly voice offering solace and quiet warmth", pitchMultiplier = 0.99f, speedMultiplier = 0.83f),
                VoiceProfile("whisper_14", "Ezra Brooks", "Male", "Elder", "US", "en-US", "Soft Whisper", "Gentle grandfather sharing peaceful campfire memories", pitchMultiplier = 0.86f, speedMultiplier = 0.82f),
                VoiceProfile("whisper_15", "Ophelia Grey", "Female", "Adult", "UK", "en-GB", "Soft Whisper", "Hauntingly gentle gothic ghost story whisperer", pitchMultiplier = 1.03f, speedMultiplier = 0.81f),
                VoiceProfile("whisper_16", "Zenith Skye", "Female", "Youth", "US", "en-US", "Meditation", "Morning affirmation guide inspiring calm inner focus", pitchMultiplier = 1.12f, speedMultiplier = 0.85f),
                VoiceProfile("whisper_17", "Aiden Rowe", "Male", "Adult", "Australia", "en-AU", "Meditation", "Coastal wave meditation host with soothing rhythm", pitchMultiplier = 0.94f, speedMultiplier = 0.83f),
                VoiceProfile("whisper_18", "Talia Breeze", "Female", "Adult", "US", "en-US", "Soft Whisper", "Gentle ASMR ear-to-ear cozy reading experience", pitchMultiplier = 1.09f, speedMultiplier = 0.79f),
                VoiceProfile("whisper_19", "Elijah Peace", "Male", "Mature", "US", "en-US", "Meditation", "Stress-reduction breathing tempo instructor", pitchMultiplier = 0.88f, speedMultiplier = 0.80f),
                VoiceProfile("whisper_20", "Vesper Song", "Female", "Adult", "Global", "en-US", "Soft Whisper", "Twilight poetry reader under ambient rain sounds", pitchMultiplier = 1.04f, speedMultiplier = 0.78f)
            )
        )

        // 5. COMMERCIAL & BRAND PROMO (20 voices)
        list.addAll(
            listOf(
                VoiceProfile("comm_01", "Max Sterling", "Male", "Adult", "US", "en-US", "Commercial", "Punchy, persuasive automotive and sports commercial voiceover", pitchMultiplier = 0.98f, speedMultiplier = 1.10f),
                VoiceProfile("comm_02", "Vanessa Luxe", "Female", "Adult", "US", "en-US", "Commercial", "Silky luxury perfume and high-fashion brand ambassador", pitchMultiplier = 1.06f, speedMultiplier = 0.96f),
                VoiceProfile("comm_03", "Trent Dawson", "Male", "Adult", "US", "en-US", "Commercial", "Upbeat tech gadget and smartphone product reveal voice", pitchMultiplier = 1.02f, speedMultiplier = 1.08f),
                VoiceProfile("comm_04", "Jessica Sterling", "Female", "Adult", "UK", "en-GB", "Commercial", "Sophisticated British retail and financial services voice", pitchMultiplier = 1.04f, speedMultiplier = 1.01f),
                VoiceProfile("comm_05", "Carter Hayes", "Male", "Adult", "US", "en-US", "Commercial", "Relatable everyday dad for grocery and family product ads", pitchMultiplier = 0.96f, speedMultiplier = 1.04f),
                VoiceProfile("comm_06", "Natalie Cross", "Female", "Adult", "US", "en-US", "Commercial", "Energetic health, fitness, and lifestyle brand voice", pitchMultiplier = 1.12f, speedMultiplier = 1.09f),
                VoiceProfile("comm_07", "Jaxon Vance", "Male", "Adult", "Australia", "en-AU", "Commercial", "Adventurous 4x4 off-road and outdoor gear spokesperson", pitchMultiplier = 0.94f, speedMultiplier = 1.07f),
                VoiceProfile("comm_08", "Sophie Laurent", "Female", "Adult", "Global", "en-US", "Commercial", "Chic cosmetic and skincare specialist with continental charm", pitchMultiplier = 1.05f, speedMultiplier = 0.97f),
                VoiceProfile("comm_09", "Brett Miller", "Male", "Adult", "US", "en-US", "Commercial", "High-octane monster truck and summer festival hype announcer", pitchMultiplier = 1.05f, speedMultiplier = 1.15f),
                VoiceProfile("comm_10", "Elena Rossi", "Female", "Adult", "Global", "en-US", "Commercial", "Premium espresso and gourmet culinary brand narration", pitchMultiplier = 1.01f, speedMultiplier = 0.98f),
                VoiceProfile("comm_11", "Grant Palmer", "Male", "Mature", "US", "en-US", "Commercial", "Trusted banking, wealth management, and insurance voice", pitchMultiplier = 0.91f, speedMultiplier = 0.99f),
                VoiceProfile("comm_12", "Lauren Scott", "Female", "Youth", "US", "en-US", "Commercial", "Trendy college fashion and app launch brand personality", pitchMultiplier = 1.16f, speedMultiplier = 1.12f),
                VoiceProfile("comm_13", "Cooper King", "Male", "Adult", "US", "en-US", "Commercial", "Craft brewery and rustic artisanal brand storyteller", pitchMultiplier = 0.93f, speedMultiplier = 1.01f),
                VoiceProfile("comm_14", "Gemma Ward", "Female", "Adult", "UK", "en-GB", "Commercial", "Crisp e-commerce promotional narrator with British polish", pitchMultiplier = 1.07f, speedMultiplier = 1.05f),
                VoiceProfile("comm_15", "Devon Miles", "Male", "Adult", "US", "en-US", "Commercial", "Sleek electric vehicle innovation showcase voice", pitchMultiplier = 0.97f, speedMultiplier = 1.03f),
                VoiceProfile("comm_16", "Cassidy Lake", "Female", "Adult", "US", "en-US", "Commercial", "Friendly organic supermarket and farm-fresh advocate", pitchMultiplier = 1.09f, speedMultiplier = 1.04f),
                VoiceProfile("comm_17", "Rory MacIntyre", "Male", "Adult", "Scotland", "en-GB", "Commercial", "Single-malt scotch and historic heritage brand voice", pitchMultiplier = 0.92f, speedMultiplier = 0.96f),
                VoiceProfile("comm_18", "Tess Montgomery", "Female", "Adult", "Australia", "en-AU", "Commercial", "Sunny tourism board and airline promotional ambassador", pitchMultiplier = 1.08f, speedMultiplier = 1.06f),
                VoiceProfile("comm_19", "Dominic Fox", "Male", "Adult", "UK", "en-GB", "Commercial", "High-end watchmaker and horology advertising voice", pitchMultiplier = 0.95f, speedMultiplier = 0.98f),
                VoiceProfile("comm_20", "Kendall Blair", "Female", "Adult", "US", "en-US", "Commercial", "Clear, concise direct-to-consumer software explainer voice", pitchMultiplier = 1.06f, speedMultiplier = 1.07f)
            )
        )

        // 6. NEWSREADERS & BROADCAST (20 voices)
        list.addAll(
            listOf(
                VoiceProfile("news_01", "Walter Cronkite", "Male", "Mature", "US", "en-US", "News", "Unflappable prime-time national evening news anchor", pitchMultiplier = 0.94f, speedMultiplier = 1.02f),
                VoiceProfile("news_02", "Catherine Sterling", "Female", "Mature", "UK", "en-GB", "News", "BBC World Service lead foreign correspondent", pitchMultiplier = 1.0f, speedMultiplier = 1.03f),
                VoiceProfile("news_03", "James Holloway", "Male", "Adult", "US", "en-US", "News", "Financial market closing bell desk reporter", pitchMultiplier = 0.97f, speedMultiplier = 1.08f),
                VoiceProfile("news_04", "Farah Siddiqui", "Female", "Adult", "Global", "en-US", "News", "Diplomatic summit and global treaty news specialist", pitchMultiplier = 1.04f, speedMultiplier = 1.01f),
                VoiceProfile("news_05", "Brian O'Hanlon", "Male", "Adult", "Ireland", "en-IE", "News", "Morning radio current affairs anchor with steady poise", pitchMultiplier = 0.96f, speedMultiplier = 1.03f),
                VoiceProfile("news_06", "Diana Prince", "Female", "Adult", "US", "en-US", "News", "Investigative special assignment television journalist", pitchMultiplier = 1.02f, speedMultiplier = 1.0f),
                VoiceProfile("news_07", "Hugh Lancaster", "Male", "Mature", "UK", "en-GB", "News", "Parliamentary reporter breaking election bulletins", pitchMultiplier = 0.92f, speedMultiplier = 1.04f),
                VoiceProfile("news_08", "Natasha Romanova", "Female", "Adult", "Global", "en-US", "News", "Eastern Europe geopolitical crisis desk commentator", pitchMultiplier = 1.01f, speedMultiplier = 1.02f),
                VoiceProfile("news_09", "Vikram Sen", "Male", "Adult", "India", "en-IN", "News", "Prime-time Indian national news hour anchor", pitchMultiplier = 0.98f, speedMultiplier = 1.05f),
                VoiceProfile("news_10", "Angela Miller", "Female", "Adult", "US", "en-US", "News", "Weather center meteorologist live storm update broadcaster", pitchMultiplier = 1.09f, speedMultiplier = 1.07f),
                VoiceProfile("news_11", "Thomas Burke", "Male", "Elder", "US", "en-US", "News", "Veteran presidential press corps reporter", pitchMultiplier = 0.91f, speedMultiplier = 0.99f),
                VoiceProfile("news_12", "Ingrid Lind", "Female", "Adult", "Global", "en-US", "News", "Stockholm international science Nobel prize reporter", pitchMultiplier = 1.03f, speedMultiplier = 1.0f),
                VoiceProfile("news_13", "Marcus Sterling", "Male", "Adult", "Canada", "en-CA", "News", "Canadian national public broadcasting anchor", pitchMultiplier = 0.95f, speedMultiplier = 1.01f),
                VoiceProfile("news_14", "Rachel Scott", "Female", "Adult", "Australia", "en-AU", "News", "Sydney national midday news bulletin presenter", pitchMultiplier = 1.05f, speedMultiplier = 1.04f),
                VoiceProfile("news_15", "Damian Vance", "Male", "Adult", "US", "en-US", "News", "Breaking wire report anchor with urgent tempo", pitchMultiplier = 0.99f, speedMultiplier = 1.10f),
                VoiceProfile("news_16", "Mei-Ling Zhou", "Female", "Adult", "Global", "en-US", "News", "Asia-Pacific economy and shipping desk broadcaster", pitchMultiplier = 1.06f, speedMultiplier = 1.02f),
                VoiceProfile("news_17", "Gareth Evans", "Male", "Adult", "UK", "en-GB", "News", "Wales national assembly and legal affairs reporter", pitchMultiplier = 0.96f, speedMultiplier = 1.01f),
                VoiceProfile("news_18", "Courtney Shaw", "Female", "Youth", "US", "en-US", "News", "Modern digital-first news flash and ticker voice", pitchMultiplier = 1.11f, speedMultiplier = 1.12f),
                VoiceProfile("news_19", "Preston Vance", "Male", "Mature", "US", "en-US", "News", "Sunday morning political round-table moderator", pitchMultiplier = 0.93f, speedMultiplier = 0.98f),
                VoiceProfile("news_20", "Zoe Alston", "Female", "Adult", "UK", "en-GB", "News", "Tech sector venture capital news roundup host", pitchMultiplier = 1.04f, speedMultiplier = 1.06f)
            )
        )

        // 7. CARTOON, ANIME & YOUTH (25 voices)
        list.addAll(
            listOf(
                VoiceProfile("toon_01", "Pip Squeak", "Youth", "Youth", "US", "en-US", "Cartoon", "Playful mischievous cartoon squirrel with bouncy giggle", pitchMultiplier = 1.55f, speedMultiplier = 1.20f),
                VoiceProfile("toon_02", "Barnaby Bear", "Male", "Adult", "US", "en-US", "Cartoon", "Goofy lovable clumsy forest bear with chuckle", pitchMultiplier = 0.82f, speedMultiplier = 0.92f),
                VoiceProfile("toon_03", "Sparky Rabbit", "Youth", "Youth", "US", "en-US", "Cartoon", "Speedy hyperactive cartoon bunny running laps", pitchMultiplier = 1.48f, speedMultiplier = 1.28f),
                VoiceProfile("toon_04", "Princess Aurelia", "Female", "Youth", "UK", "en-GB", "Cartoon", "Enchanted fairy-tale royal princess talking to songbirds", pitchMultiplier = 1.24f, speedMultiplier = 1.02f),
                VoiceProfile("toon_05", "Grommet the Goblin", "Male", "Adult", "UK", "en-GB", "Cartoon", "Cackling cave goblin tinkering with exploding gadgets", pitchMultiplier = 1.35f, speedMultiplier = 1.18f),
                VoiceProfile("toon_06", "Professor Whiskers", "Male", "Elder", "UK", "en-GB", "Cartoon", "Scholarly owl teaching magical arithmetic in the forest", pitchMultiplier = 0.92f, speedMultiplier = 0.94f),
                VoiceProfile("toon_07", "Luna Starling", "Female", "Youth", "US", "en-US", "Cartoon", "Brave anime magical girl summoning cosmic shields", pitchMultiplier = 1.30f, speedMultiplier = 1.12f),
                VoiceProfile("toon_08", "Kenji Dragon", "Male", "Youth", "Global", "en-US", "Cartoon", "Spirited shonen anime protagonist vowing to never give up", pitchMultiplier = 1.18f, speedMultiplier = 1.15f),
                VoiceProfile("toon_09", "Granny Applebee", "Female", "Elder", "US", "en-US", "Cartoon", "Whimsical pie-baking country hedgehog grandmother", pitchMultiplier = 1.28f, speedMultiplier = 0.90f),
                VoiceProfile("toon_10", "Bolt the Robot", "Male", "Adult", "US", "en-US", "Cartoon", "Beeping cheerful tin robot exploring friendship", pitchMultiplier = 1.10f, speedMultiplier = 1.05f),
                VoiceProfile("toon_11", "Captain Clackers", "Male", "Mature", "UK", "en-GB", "Cartoon", "Scurvy cartoon pirate captain with a wooden peg-leg squawk", pitchMultiplier = 0.87f, speedMultiplier = 1.04f),
                VoiceProfile("toon_12", "Mimi Kitten", "Female", "Youth", "US", "en-US", "Cartoon", "Ultra-sweet curious kitten discovering yarn balls", pitchMultiplier = 1.60f, speedMultiplier = 1.12f),
                VoiceProfile("toon_13", "Dino Rex", "Male", "Youth", "US", "en-US", "Cartoon", "Friendly baby T-Rex practicing a mighty little roar", pitchMultiplier = 1.15f, speedMultiplier = 1.06f),
                VoiceProfile("toon_14", "Wanda Witchlet", "Female", "Youth", "UK", "en-GB", "Cartoon", "Apprentice spellcaster making cauldrons bubble purple", pitchMultiplier = 1.34f, speedMultiplier = 1.14f),
                VoiceProfile("toon_15", "Olaf the Troll", "Male", "Mature", "Global", "en-US", "Cartoon", "Slow-talking gentle bridge troll who loves riddles", pitchMultiplier = 0.76f, speedMultiplier = 0.84f),
                VoiceProfile("toon_16", "Flutter Fairy", "Female", "Youth", "Ireland", "en-IE", "Cartoon", "Sparkling clover fairy leaving glowing dust trails", pitchMultiplier = 1.45f, speedMultiplier = 1.16f),
                VoiceProfile("toon_17", "Sheriff Scruff", "Male", "Mature", "US", "en-US", "Cartoon", "Gruff animated bulldog keeping peace in Toontown", pitchMultiplier = 0.84f, speedMultiplier = 0.96f),
                VoiceProfile("toon_18", "Ziggy Alien", "Youth", "Youth", "Global", "en-US", "Cartoon", "Three-eyed extraterrestrial tourist visiting planet Earth", pitchMultiplier = 1.50f, speedMultiplier = 1.22f),
                VoiceProfile("toon_19", "Baroness Von Von", "Female", "Mature", "Global", "en-US", "Cartoon", "Melodramatic cartoon villainess with dramatic sigh", pitchMultiplier = 1.18f, speedMultiplier = 0.98f),
                VoiceProfile("toon_20", "Timmy Adventurer", "Male", "Youth", "US", "en-US", "Cartoon", "Curious eight-year-old treehouse club commander", pitchMultiplier = 1.38f, speedMultiplier = 1.10f),
                VoiceProfile("toon_21", "Penny Pennyworth", "Female", "Youth", "US", "en-US", "Cartoon", "Nerdy young science fair prodigy solving mysteries", pitchMultiplier = 1.32f, speedMultiplier = 1.15f),
                VoiceProfile("toon_22", "Snort the Dragon", "Male", "Youth", "UK", "en-GB", "Cartoon", "Teacup dragon who only sneezes harmless blue sparks", pitchMultiplier = 1.42f, speedMultiplier = 1.10f),
                VoiceProfile("toon_23", "Madame Croak", "Female", "Elder", "US", "en-US", "Cartoon", "Aristocratic swamp frog hosting tea parties in lilies", pitchMultiplier = 0.95f, speedMultiplier = 0.88f),
                VoiceProfile("toon_24", "Cosmo Cadet", "Male", "Youth", "US", "en-US", "Cartoon", "Eager junior rocket pilot flying through the asteroid belt", pitchMultiplier = 1.25f, speedMultiplier = 1.14f),
                VoiceProfile("toon_25", "Bella Butterfly", "Female", "Youth", "US", "en-US", "Cartoon", "Cheerful pollinator spreading spring garden cheer", pitchMultiplier = 1.52f, speedMultiplier = 1.10f)
            )
        )

        // 8. FANTASY & SCI-FI CHARACTERS (25 voices)
        list.addAll(
            listOf(
                VoiceProfile("sci_01", "AI Core 9000", "Character", "Adult", "Global", "en-US", "Fantasy", "Precise synthesized starship mainframe operating on logic", pitchMultiplier = 1.0f, speedMultiplier = 1.02f),
                VoiceProfile("sci_02", "Lord Malakor", "Male", "Mature", "UK", "en-GB", "Fantasy", "Dark necromancer sovereign demanding tribute before siege", pitchMultiplier = 0.77f, speedMultiplier = 0.89f),
                VoiceProfile("sci_03", "Elven Queen Yvaine", "Female", "Mature", "UK", "en-GB", "Fantasy", "Immortal woodland empress speaking from crystal spires", pitchMultiplier = 1.08f, speedMultiplier = 0.92f),
                VoiceProfile("sci_04", "Cyber Samurai Jin", "Male", "Adult", "Global", "en-US", "Fantasy", "Honor-bound ronin augmented with neon cybernetics", pitchMultiplier = 0.91f, speedMultiplier = 0.98f),
                VoiceProfile("sci_05", "Sorceress Vespera", "Female", "Adult", "US", "en-US", "Fantasy", "Shadow weaver weaving incantations under the eclipse", pitchMultiplier = 0.98f, speedMultiplier = 0.94f),
                VoiceProfile("sci_06", "Dwarven King Thrain", "Male", "Elder", "Scotland", "en-GB", "Fantasy", "Iron mountain monarch roaring beside molten forges", pitchMultiplier = 0.80f, speedMultiplier = 0.96f),
                VoiceProfile("sci_07", "Android Unit 7-Echo", "Female", "Adult", "US", "en-US", "Fantasy", "Replicant awakening to emotional consciousness in rain", pitchMultiplier = 1.04f, speedMultiplier = 0.97f),
                VoiceProfile("sci_08", "Vampire Prince Lucien", "Male", "Adult", "Global", "en-US", "Fantasy", "Century-old aristocrat savoring vintage crimson wine", pitchMultiplier = 0.88f, speedMultiplier = 0.91f),
                VoiceProfile("sci_09", "Archmage Eldrin", "Male", "Elder", "UK", "en-GB", "Fantasy", "Council high wizard reciting celestial constellation runes", pitchMultiplier = 0.84f, speedMultiplier = 0.89f),
                VoiceProfile("sci_10", "Valkyrie Freydis", "Female", "Adult", "Global", "en-US", "Fantasy", "Fierce warrior maiden summoning shields to Valhalla", pitchMultiplier = 1.05f, speedMultiplier = 1.04f),
                VoiceProfile("sci_11", "Chronomancer Thorne", "Male", "Mature", "UK", "en-GB", "Fantasy", "Time traveler witnessing alternate future collapse", pitchMultiplier = 0.92f, speedMultiplier = 0.95f),
                VoiceProfile("sci_12", "Siren of the Depths", "Female", "Adult", "Global", "en-US", "Fantasy", "Beguiling aquatic enchantress calling from the abyss", pitchMultiplier = 1.10f, speedMultiplier = 0.88f),
                VoiceProfile("sci_13", "Cyberpunk Hacker Jax", "Male", "Youth", "US", "en-US", "Fantasy", "Rogue decker breaking corporate ICE firewalls", pitchMultiplier = 1.09f, speedMultiplier = 1.15f),
                VoiceProfile("sci_14", "Grand Oracle Delphi", "Female", "Elder", "Global", "en-US", "Fantasy", "Blind temple seer deciphering fate through burning incense", pitchMultiplier = 0.96f, speedMultiplier = 0.86f),
                VoiceProfile("sci_15", "Space Mercenary Rex", "Male", "Mature", "US", "en-US", "Fantasy", "Cynical asteroid belt bounty hunter smoking a cigar", pitchMultiplier = 0.85f, speedMultiplier = 0.99f),
                VoiceProfile("sci_16", "Mech Pilot Sarah", "Female", "Adult", "US", "en-US", "Fantasy", "Cockpit fighter locking plasma cannons on alien dreadnought", pitchMultiplier = 1.07f, speedMultiplier = 1.10f),
                VoiceProfile("sci_17", "Gargoyle Guardian", "Male", "Elder", "UK", "en-GB", "Fantasy", "Stone sentinel awakening at twilight to guard the cathedral", pitchMultiplier = 0.72f, speedMultiplier = 0.82f),
                VoiceProfile("sci_18", "Banshee Sorrow", "Female", "Adult", "Ireland", "en-IE", "Fantasy", "Wailing spirit foretelling tragedy across misty moors", pitchMultiplier = 1.15f, speedMultiplier = 0.92f),
                VoiceProfile("sci_19", "Sublight Navigator", "Male", "Adult", "Canada", "en-CA", "Fantasy", "Interstellar jump specialist calculating warp coordinates", pitchMultiplier = 0.95f, speedMultiplier = 1.01f),
                VoiceProfile("sci_20", "Goblin Merchant Skrit", "Male", "Adult", "Global", "en-US", "Fantasy", "Greedy bazaar peddler offering cursed rare relics", pitchMultiplier = 1.30f, speedMultiplier = 1.20f),
                VoiceProfile("sci_21", "Dragon Rider Katherine", "Female", "Adult", "UK", "en-GB", "Fantasy", "Sky rider banking her ruby dragon through storm clouds", pitchMultiplier = 1.06f, speedMultiplier = 1.06f),
                VoiceProfile("sci_22", "Quantum Entity 0", "Character", "Adult", "Global", "en-US", "Fantasy", "Multidimensional consciousness speaking in harmonic polyphony", pitchMultiplier = 0.90f, speedMultiplier = 0.95f),
                VoiceProfile("sci_23", "High Inquisitor Morzan", "Male", "Mature", "Global", "en-US", "Fantasy", "Fanatical zealot rooting out heretical alchemy", pitchMultiplier = 0.86f, speedMultiplier = 0.97f),
                VoiceProfile("sci_24", "Star Nomad Lyra", "Female", "Youth", "US", "en-US", "Fantasy", "Solar surfer collecting deuterium fuel from solar flares", pitchMultiplier = 1.14f, speedMultiplier = 1.08f),
                VoiceProfile("sci_25", "Ancient Golem", "Male", "Elder", "Global", "en-US", "Fantasy", "Earthen titan rumbling with seismic bass vibrations", pitchMultiplier = 0.65f, speedMultiplier = 0.78f)
            )
        )

        // 9. DRAMATIC STORYTELLERS & AUDIOBOOKS (25 voices)
        list.addAll(
            listOf(
                VoiceProfile("story_01", "Gwendolyn Thorne", "Female", "Mature", "UK", "en-GB", "Narration", "Classic Victorian Gothic romance and mystery narrator", pitchMultiplier = 0.98f, speedMultiplier = 0.93f),
                VoiceProfile("story_02", "Jonathan Harker", "Male", "Adult", "UK", "en-GB", "Narration", "Intense psychological thriller reader with rising dread", pitchMultiplier = 0.95f, speedMultiplier = 0.96f),
                VoiceProfile("story_03", "Cordelia Blackwood", "Female", "Mature", "US", "en-US", "Narration", "Dark southern gothic novelist reciting family secrets", pitchMultiplier = 0.96f, speedMultiplier = 0.92f),
                VoiceProfile("story_04", "Rupert Kingsley", "Male", "Elder", "UK", "en-GB", "Narration", "Sherlockian detective fiction narrator with intellectual zest", pitchMultiplier = 0.90f, speedMultiplier = 0.97f),
                VoiceProfile("story_05", "Madeleine Dupond", "Female", "Adult", "Global", "en-US", "Narration", "Sensual atmospheric romance reader with French cadence", pitchMultiplier = 1.03f, speedMultiplier = 0.94f),
                VoiceProfile("story_06", "Edgar Vane", "Male", "Mature", "US", "en-US", "Narration", "Poe-inspired dark horror narrator delivering macabre prose", pitchMultiplier = 0.88f, speedMultiplier = 0.88f),
                VoiceProfile("story_07", "Rowan O'Shea", "Male", "Adult", "Ireland", "en-IE", "Narration", "Mythological bard chanting ancient hero ballads", pitchMultiplier = 0.93f, speedMultiplier = 0.95f),
                VoiceProfile("story_08", "Astrid Lindgren", "Female", "Elder", "Global", "en-US", "Narration", "Wholesome bedtime fable storyteller loved by generations", pitchMultiplier = 1.05f, speedMultiplier = 0.90f),
                VoiceProfile("story_09", "Dashiell Marlowe", "Male", "Adult", "US", "en-US", "Narration", "1940s hardboiled noir private eye voice with cigarette rasp", pitchMultiplier = 0.86f, speedMultiplier = 0.94f),
                VoiceProfile("story_10", "Constance Sterling", "Female", "Mature", "UK", "en-GB", "Narration", "Regency era high-society drama and matchmaking novel reader", pitchMultiplier = 1.02f, speedMultiplier = 0.96f),
                VoiceProfile("story_11", "Tobias Croft", "Male", "Elder", "UK", "en-GB", "Narration", "Haunted lighthouse keeper recording logbook entries", pitchMultiplier = 0.82f, speedMultiplier = 0.87f),
                VoiceProfile("story_12", "Seraphine Frost", "Female", "Adult", "US", "en-US", "Narration", "Post-apocalyptic survival memoir protagonist", pitchMultiplier = 1.01f, speedMultiplier = 0.98f),
                VoiceProfile("story_13", "Benton Carlisle", "Male", "Mature", "US", "en-US", "Narration", "Epic Western frontier novel narrator", pitchMultiplier = 0.87f, speedMultiplier = 0.91f),
                VoiceProfile("story_14", "Helena Brandt", "Female", "Adult", "Global", "en-US", "Narration", "Cold War espionage suspense reader with ice-cool clarity", pitchMultiplier = 0.99f, speedMultiplier = 0.97f),
                VoiceProfile("story_15", "Seamus Finn", "Male", "Adult", "Ireland", "en-IE", "Narration", "Lively Irish village comedy and folklore humorist", pitchMultiplier = 0.97f, speedMultiplier = 1.02f),
                VoiceProfile("story_16", "Cassandra Ward", "Female", "Mature", "US", "en-US", "Narration", "Mythological Greek tragedy chorus leader", pitchMultiplier = 0.97f, speedMultiplier = 0.90f),
                VoiceProfile("story_17", "Garrick Stone", "Male", "Mature", "UK", "en-GB", "Narration", "Medieval siege and knightly crusade chronicle narrator", pitchMultiplier = 0.89f, speedMultiplier = 0.93f),
                VoiceProfile("story_18", "Penelope Vance", "Female", "Adult", "UK", "en-GB", "Narration", "Cottagecore woodland mystery cozy reader", pitchMultiplier = 1.06f, speedMultiplier = 0.96f),
                VoiceProfile("story_19", "Nigel Thornton", "Male", "Mature", "UK", "en-GB", "Narration", "Grand opera house historian recounting prima donna feuds", pitchMultiplier = 0.94f, speedMultiplier = 0.95f),
                VoiceProfile("story_20", "Tabitha Crane", "Female", "Adult", "US", "en-US", "Narration", "New England maritime ghost lore storyteller", pitchMultiplier = 1.03f, speedMultiplier = 0.92f),
                VoiceProfile("story_21", "Silas Thorne", "Male", "Elder", "US", "en-US", "Narration", "Appalachian mountain folktale picker and story spinner", pitchMultiplier = 0.84f, speedMultiplier = 0.89f),
                VoiceProfile("story_22", "Isolde Dubois", "Female", "Adult", "Global", "en-US", "Narration", "Impressionist Paris bohemian artist journal reader", pitchMultiplier = 1.04f, speedMultiplier = 0.95f),
                VoiceProfile("story_23", "Kendrick Boyd", "Male", "Adult", "US", "en-US", "Narration", "Urban detective thriller with brisk pacing and street sense", pitchMultiplier = 0.94f, speedMultiplier = 1.03f),
                VoiceProfile("story_24", "Valerie St. Clair", "Female", "Mature", "US", "en-US", "Narration", "Golden Age Hollywood gossip memoirist", pitchMultiplier = 1.0f, speedMultiplier = 0.99f),
                VoiceProfile("story_25", "Zachary Stone", "Male", "Adult", "Australia", "en-AU", "Narration", "Outback bush survival adventure fiction narrator", pitchMultiplier = 0.93f, speedMultiplier = 0.99f)
            )
        )

        // 10. MULTILINGUAL & GLOBAL ACCENTS (40 voices: Hindi, Urdu, Spanish, French, German, Japanese, etc.)
        list.addAll(
            listOf(
                // Hindi & Urdu (explicitly required in prompt)
                VoiceProfile("lang_hi_01", "Aarav Sharma", "Male", "Adult", "India", "hi-IN", "Conversational", "Fluent modern Hindi narrator with clear Delhi cadence", pitchMultiplier = 0.98f, speedMultiplier = 1.0f),
                VoiceProfile("lang_hi_02", "Diya Patel", "Female", "Adult", "India", "hi-IN", "Narration", "Melodious Hindi storyteller with expressive emotional range", pitchMultiplier = 1.08f, speedMultiplier = 0.96f),
                VoiceProfile("lang_hi_03", "Vikramaditya Roy", "Male", "Mature", "India", "hi-IN", "Deep Bass", "Deep resonant Hindi classical narrator for historical epics", pitchMultiplier = 0.86f, speedMultiplier = 0.94f),
                VoiceProfile("lang_hi_04", "Ananya Deshmukh", "Female", "Adult", "India", "hi-IN", "Commercial", "Upbeat Hindi brand promo voice for lifestyle & commerce", pitchMultiplier = 1.10f, speedMultiplier = 1.05f),
                VoiceProfile("lang_ur_01", "Hamza Qureshi", "Male", "Adult", "Global", "ur-PK", "Narration", "Eloquent Urdu literary narrator with pristine pronunciation", pitchMultiplier = 0.96f, speedMultiplier = 0.97f),
                VoiceProfile("lang_ur_02", "Zainab Fatima", "Female", "Adult", "Global", "ur-PK", "Conversational", "Warm, cultured Urdu podcast host and poetry reciter", pitchMultiplier = 1.06f, speedMultiplier = 0.98f),
                VoiceProfile("lang_ur_03", "Tariq Mahmood", "Male", "Mature", "Global", "ur-PK", "Deep Bass", "Resonant Urdu news anchor and documentary voiceover", pitchMultiplier = 0.88f, speedMultiplier = 0.95f),
                VoiceProfile("lang_ur_04", "Maryam Bilal", "Female", "Youth", "Global", "ur-PK", "Cartoon", "Lively Urdu storytelling voice for children's stories", pitchMultiplier = 1.25f, speedMultiplier = 1.08f),

                // Spanish
                VoiceProfile("lang_es_01", "Mateo Morales", "Male", "Adult", "Global", "es-ES", "Narration", "Castilian Spanish literary narrator with crisp cadence", pitchMultiplier = 0.96f, speedMultiplier = 1.02f),
                VoiceProfile("lang_es_02", "Lucia Navarro", "Female", "Adult", "Global", "es-ES", "Commercial", "Vibrant Madrid commercial voiceover with modern flair", pitchMultiplier = 1.08f, speedMultiplier = 1.05f),
                VoiceProfile("lang_es_03", "Alejandro Rios", "Male", "Adult", "Global", "es-MX", "Conversational", "Friendly Mexican Spanish podcast host with natural warmth", pitchMultiplier = 0.98f, speedMultiplier = 1.03f),
                VoiceProfile("lang_es_04", "Valentina Cruz", "Female", "Adult", "Global", "es-MX", "Narration", "Expressive Latin American telenovela and story reader", pitchMultiplier = 1.05f, speedMultiplier = 0.98f),

                // French
                VoiceProfile("lang_fr_01", "Louis Mercier", "Male", "Adult", "Global", "fr-FR", "Narration", "Parisian documentary narrator with sophisticated timbre", pitchMultiplier = 0.94f, speedMultiplier = 0.98f),
                VoiceProfile("lang_fr_02", "Amelie Laurent", "Female", "Adult", "Global", "fr-FR", "Commercial", "Silky French luxury brand and cosmetic ambassador", pitchMultiplier = 1.06f, speedMultiplier = 0.97f),
                VoiceProfile("lang_fr_03", "Etienne Beauchamp", "Male", "Mature", "Canada", "fr-CA", "Conversational", "Warm Quebecois radio presenter with authentic tone", pitchMultiplier = 0.92f, speedMultiplier = 1.0f),
                VoiceProfile("lang_fr_04", "Chloe Desjardins", "Female", "Youth", "Canada", "fr-CA", "Cartoon", "Energetic French-Canadian animated cartoon character", pitchMultiplier = 1.30f, speedMultiplier = 1.12f),

                // German
                VoiceProfile("lang_de_01", "Maximilian Weber", "Male", "Adult", "Global", "de-DE", "Narration", "Precise German engineering documentary narrator", pitchMultiplier = 0.92f, speedMultiplier = 1.0f),
                VoiceProfile("lang_de_02", "Hanna Fischer", "Female", "Adult", "Global", "de-DE", "News", "Crisp German television news anchor with articulate delivery", pitchMultiplier = 1.04f, speedMultiplier = 1.02f),
                VoiceProfile("lang_de_03", "Lukas Richter", "Male", "Adult", "Global", "de-DE", "Commercial", "Dynamic automotive and sports voiceover in German", pitchMultiplier = 0.97f, speedMultiplier = 1.08f),
                VoiceProfile("lang_de_04", "Greta Schneider", "Female", "Mature", "Global", "de-DE", "Narration", "Warm German literary fiction reader", pitchMultiplier = 0.98f, speedMultiplier = 0.95f),

                // Japanese
                VoiceProfile("lang_ja_01", "Ren Takahashi", "Male", "Adult", "Global", "ja-JP", "Narration", "Poised Japanese documentary and commercial voice", pitchMultiplier = 0.98f, speedMultiplier = 1.0f),
                VoiceProfile("lang_ja_02", "Yui Sakamoto", "Female", "Adult", "Global", "ja-JP", "Conversational", "Gentle Japanese conversational podcast host", pitchMultiplier = 1.12f, speedMultiplier = 1.02f),
                VoiceProfile("lang_ja_03", "Daiki Sato", "Male", "Youth", "Global", "ja-JP", "Cartoon", "High-energy anime protagonist with passionate battle cry", pitchMultiplier = 1.20f, speedMultiplier = 1.15f),
                VoiceProfile("lang_ja_04", "Aoi Matsumoto", "Female", "Youth", "Global", "ja-JP", "Cartoon", "Sweet shojo anime companion with cute inflection", pitchMultiplier = 1.35f, speedMultiplier = 1.10f),

                // Italian, Portuguese, Arabic, Chinese
                VoiceProfile("lang_it_01", "Marco Bellini", "Male", "Adult", "Global", "it-IT", "Commercial", "Expressive Italian culinary and travel ambassador", pitchMultiplier = 0.97f, speedMultiplier = 1.04f),
                VoiceProfile("lang_it_02", "Giulia Romano", "Female", "Adult", "Global", "it-IT", "Narration", "Lyrical Italian art history narrator", pitchMultiplier = 1.05f, speedMultiplier = 0.99f),
                VoiceProfile("lang_pt_01", "Thiago Silva", "Male", "Adult", "Global", "pt-BR", "Conversational", "Warm Brazilian Portuguese podcast host with natural groove", pitchMultiplier = 0.96f, speedMultiplier = 1.05f),
                VoiceProfile("lang_pt_02", "Beatriz Santos", "Female", "Adult", "Global", "pt-BR", "Commercial", "Upbeat Brazilian lifestyle and retail promo voice", pitchMultiplier = 1.08f, speedMultiplier = 1.06f),
                VoiceProfile("lang_ar_01", "Omar Farooq", "Male", "Mature", "Global", "ar-SA", "Narration", "Deep classical Arabic narrator for documentary features", pitchMultiplier = 0.88f, speedMultiplier = 0.96f),
                VoiceProfile("lang_ar_02", "Layla Mansoor", "Female", "Adult", "Global", "ar-SA", "News", "Authoritative Arabic broadcast news anchor", pitchMultiplier = 1.04f, speedMultiplier = 1.01f),
                VoiceProfile("lang_zh_01", "Wei Chen", "Male", "Adult", "Global", "zh-CN", "Narration", "Articulate Mandarin documentary presenter", pitchMultiplier = 0.98f, speedMultiplier = 1.02f),
                VoiceProfile("lang_zh_02", "Li Na", "Female", "Adult", "Global", "zh-CN", "Commercial", "Graceful Mandarin brand showcase voice", pitchMultiplier = 1.09f, speedMultiplier = 1.04f),

                // Regional English Accents (Australian, Irish, Scottish, Indian, South African)
                VoiceProfile("eng_au_01", "Cooper Hayes", "Male", "Adult", "Australia", "en-AU", "Conversational", "Authentic Melbourne barista and creative podcaster", pitchMultiplier = 0.98f, speedMultiplier = 1.04f),
                VoiceProfile("eng_au_02", "Matilda Wright", "Female", "Adult", "Australia", "en-AU", "Commercial", "Bright Sydney tech brand voice", pitchMultiplier = 1.07f, speedMultiplier = 1.06f),
                VoiceProfile("eng_ie_01", "Cillian Murphy Jr", "Male", "Adult", "Ireland", "en-IE", "Narration", "Intense Dublin stage actor reading drama", pitchMultiplier = 0.94f, speedMultiplier = 0.96f),
                VoiceProfile("eng_ie_02", "Maeve Connell", "Female", "Adult", "Ireland", "en-IE", "Conversational", "Witty Galway writer recounting seaside anecdotes", pitchMultiplier = 1.05f, speedMultiplier = 1.01f),
                VoiceProfile("eng_sc_01", "Callum Fraser", "Male", "Mature", "Scotland", "en-GB", "Deep Bass", "Edinburgh professor discussing geology and stone", pitchMultiplier = 0.85f, speedMultiplier = 0.92f),
                VoiceProfile("eng_sc_02", "Eilidh Campbell", "Female", "Adult", "Scotland", "en-GB", "Narration", "Lyrical Isle of Skye folk historian", pitchMultiplier = 1.06f, speedMultiplier = 0.95f),
                VoiceProfile("eng_za_01", "Kagiso van Zyl", "Male", "Adult", "Global", "en-US", "Narration", "South African safari guide narrating savanna wildlife", pitchMultiplier = 0.93f, speedMultiplier = 0.98f),
                VoiceProfile("eng_za_02", "Thandiwe Khumalo", "Female", "Adult", "Global", "en-US", "Conversational", "Warm Johannesburg lifestyle and music podcast host", pitchMultiplier = 1.04f, speedMultiplier = 1.02f)
            )
        )

        // Total voices in the catalog: 25 + 25 + 20 + 20 + 20 + 20 + 25 + 25 + 25 + 40 = 245 genuinely distinct voices!
        list
    }

    fun getVoiceById(id: String): VoiceProfile {
        return allVoices.find { it.id == id } ?: allVoices.first()
    }
}
