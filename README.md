AI Health News Curator 🩺
An Android application built with Kotlin and Jetpack Compose that uses the Google Gemini API to generate and summarize health-related news articles.

📸 App Screenshots
News Feed (Screen 1)
![W1](https://github.com/user-attachments/assets/eedadcc6-9687-4c36-82fc-7f3c1c8bb2d8)
![W2](https://github.com/user-attachments/assets/125a26f4-a532-4297-a604-83447174b13e)

Article Summary (Screen 2)
![W6](https://github.com/user-attachments/assets/1a2c5ee5-c023-4341-bd7a-67d25ed9dce6)

![W7](https://github.com/user-attachments/assets/9da3aad4-5cbc-4fde-894c-f3ed4b00f6e7)

🏛️ Architecture & State Management
This project follows Google's recommended modern Android app architecture, utilizing the MVVM (Model-View-ViewModel) pattern.

Architecture Layers
UI Layer (ui package): Built entirely with Jetpack Compose. This layer is responsible for rendering the UI and capturing user events. It observes state from the ViewModel and is designed to be as "dumb" as possible.

MainActivity.kt: Hosts the NavHost for navigation.

NewsListScreen.kt & NewsDetailScreen.kt: Composable functions representing the app's screens.

ViewModel Layer (viewmodel package): This layer acts as the bridge between the UI and the Data layer.

NewsViewModel & NewsDetailViewModel: These are lifecycle-aware components that hold and expose UI state. They survive configuration changes (like screen rotation). They call the repository to fetch data and update their state.

Data Layer (data package): The single source of truth for the app's data.

NewsRepository: A singleton object that abstracts the data source. It decides whether to fetch data from the network (Gemini API) or serve it from an in-memory cache.

AISummaryService: A dedicated service class that handles the logic for calling the Gemini API to generate summaries.

State Management
State management is handled using a unidirectional data flow (UDF) with Kotlin Flows.

StateFlow: The ViewModels expose their state using StateFlow. This is a hot flow that holds the current state and emits updates to any collectors.

collectAsState(): The Composable UI functions use the collectAsState() extension function to observe the StateFlow from the ViewModel. When the state in the ViewModel changes, Jetpack Compose automatically triggers a recomposition of the UI with the new data. This creates a reactive and predictable UI.

🤖 Gemini Prompts & Refinements
The core AI functionality is powered by two main prompts sent to the gemini-1.5-flash model.

Prompt 1: Generating the News Feed (Screen 1)
Used in NewsRepository.kt to create the initial list of articles.

Generate a list of 12 recent, compelling health news headlines.
For each headline, provide a one-sentence description.
Return the result as a valid JSON array where each object has "id", "title", and "description".
The ID should be a unique number from 1 to 12.
Example: [{"id": 1, "title": "Headline", "description": "Description."}]
Refinement: Initially, the API returned the JSON wrapped in markdown fences (```json ... ```). The parsing logic was refined to strip these characters before converting the string to a JSONArray, preventing a JSONException.

Prompt 2: Summarizing an Article (Screen 2)
Used in AISummaryService.kt to generate the TL;DR and key takeaways.

Summarize the following news article.
Title: "$articleTitle"
Description: "$articleDescription"

Provide a response in JSON format with two keys:
1. "tldr": A 2-line "Too Long; Didn't Read" summary.
2. "takeaways": A JSON array of exactly 3 key takeaway bullet points.
Refinement: The prompt was made very specific about the JSON structure (tldr and takeaways keys) to ensure a consistent and easily parsable output. The parsing logic was also updated to strip markdown fences, which also occurred with this prompt's response.

⚠️ Known Issues & Potential Improvements
Error Handling: Currently, if the Gemini API call fails, the app either shows an empty list or a "Could not generate summary" message. A more user-friendly approach would be to display a Snackbar or a dedicated error message on the screen.

No Persistence: The news articles are fetched every time the app starts and are stored in an in-memory cache. Integrating a local database like Room would allow for offline access and a better user experience.

Hardcoded API Key Handling: While the API key is not in the source code, it's read from local.properties. For a production app, a more secure solution using a server or Google Cloud's Secret Manager would be preferable.

UI Loading State: The list screen is blank while the initial articles are loaded. A full-screen loading indicator could be implemented in the NewsViewModel and observed by the NewsListScreen.

Pagination & Pull-to-Refresh: The feed is currently a static list of 12 articles. Implementing pagination (loading more articles on scroll) and a pull-to-refresh mechanism would make the app feel more dynamic.
