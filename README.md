# NeuroNexus

> *Because the brain starts whispering long before it starts screaming.*

Early dementia detection through facial biometrics, voice analysis, and cognitive tasks — fully offline, entirely on-device, built for the people who need it most.

---

## Overview

**NeuroNexus** is a fully offline Android application for early dementia and cognitive decline detection. It uses multimodal AI across three pillars — facial biometrics, voice analysis, and cognitive task performance — to generate a longitudinal cognitive risk score, entirely on the user's device with no data ever leaving it.

Dementia affects 55 million people globally. Clinical diagnosis typically arrives 3–7 years after the first measurable signals appear. By then, the window for meaningful intervention is nearly closed. NeuroNexus closes that gap.

---

## Core Features

### Three Pillars of Assessment

**1. Facial Analysis**
Real-time biometric analysis using MediaPipe's 478-point 3D Face Mesh via CameraX:
- **EAR (Eye Aspect Ratio)** — blink rate, blink irregularity, fatigue signatures
- **MAR (Mouth Aspect Ratio)** — speech movement detection, facial masking
- **Brow reactivity** — emotional expressiveness tracked across a session
- **Head stability** — micro-tremor detection via nose-tip displacement (pitch + yaw range)
- **Reaction time** — latency between visual prompt and biometric response
- **Composite blunted affect scoring** via `FaceDementiaAnalyzer`

**2. Voice Analysis**
On-device speech processing during guided tasks (picture description, story recall):
- 30+ linguistic markers — lexical richness, information density, filler frequency, syntactic complexity, semantic drift
- Acoustic markers — pause duration, speech rate, pitch variation
- Processed via wav2vec 2.0 and BERT embeddings running through ONNX Runtime

**3. Cognitive Task Analysis**
Short, gamified neuropsychological tasks assessing:
- Immediate and delayed memory recall
- Attention and concentration
- Visuospatial reasoning
- Pattern recognition and processing speed
- Executive function — planning, sequencing, flexibility

All three pillars fuse into a single **Unified Cognitive Risk Score** tracked longitudinally over weeks and months.

---

## Accessibility

NeuroNexus is built for the people who need it most, not just those comfortable with technology:

- **Fully offline** — zero internet required, zero data transmitted, works anywhere
- **Read-aloud assistant** — every screen, instruction, and result is narrated aloud
- **Multilingual support** — Hindi, Tamil, Telugu, Bengali + more
- **Elder-friendly UI** — large text, high contrast, minimal navigation steps
- **On-device LLM** — plain-language insight summaries generated locally via Llama.cpp, narrated aloud

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin 1.9+ |
| UI | Jetpack Compose + Material Design 3 |
| Architecture | MVVM + Clean Architecture |
| Async | Kotlin Coroutines + StateFlow |
| Camera | CameraX |
| Face Mesh | MediaPipe Face Landmarker (478-point 3D) |
| Vision Processing | OpenCV |
| On-device ML | ONNX Runtime (wav2vec 2.0, BERT, risk classifier) |
| On-device LLM | Llama.cpp via RunAnywhere SDK (quantized GGUF) |
| Navigation | Jetpack Navigation Compose |
| Storage | Room + DataStore |
| Min SDK | 24 |
| Target SDK | 34/35 |

---

## Architecture
```
User Input (Camera + Microphone + Touch)
                    ↓
        Three Parallel Analysis Streams
         ↙              ↓             ↘
   FACIAL            VOICE          COGNITIVE
   CameraX           Mic Input      Task Engine
   MediaPipe         ONNX Runtime   Custom Logic
   FaceMesh          wav2vec 2.0
   EAR/MAR/Tremor    BERT Embeddings
   FaceDementiaAnalyzer   ↓              ↓
         ↓            Linguistic +   Task Score
   Biometric          Acoustic        Vector
   Feature Vector     Feature Vector
         ↓               ↓              ↓
              Multimodal Fusion Layer
                    (ONNX Runtime)
                         ↓
              On-device LLM (Llama.cpp)
              Plain-language Insight Generation
                         ↓
              Unified Cognitive Risk Score
              Severity Index + Longitudinal Graph
                         ↓
              Local Storage (Room / DataStore)
         ↙              ↓              ↘
    User View     Caregiver View   Clinician View
```

---

## Facial Analysis — Landmark Index Reference

| Feature | Landmarks Used |
|---|---|
| Left eye (EAR) | 362 (inner), 263 (outer), 387, 386 (top), 373, 374 (bottom) |
| Right eye (EAR) | 133 (inner), 33 (outer), 160, 158 (top), 144, 153 (bottom) |
| Left brow height | 295 (brow center) vs 386 (upper lid) |
| Right brow height | 65 (brow center) vs 158 (upper lid) |
| Lip corners (smile) | 61 (left), 291 (right) |
| Mouth openness | 13 (top), 14 (bottom) |
| Head pose | 1 (nose tip), 152 (chin), 362/133 (eye inner corners) |
| Left iris | 468–472 |
| Right iris | 473–477 |

---

## Risk Scoring

`FaceDementiaAnalyzer` produces a `compositeRiskScore` (0.0–1.0) from seven weighted signals:

| Signal | Weight | Clinical Basis |
|---|---|---|
| Blink Rate | 20% | Hypomimia marker — <8 BPM is Parkinson's/dementia pattern |
| Expressiveness | 20% | Masked affect — reduced spontaneous smiling |
| Mouth Mobility | 10% | Speech-related movement reduction |
| Brow Reactivity | 15% | Flat affect indicator |
| Gaze Engagement | 15% | Sustained gaze avoidance correlates with cognitive load |
| Head Engagement | 5% | Rigid presentation when both pitch + yaw are static |
| Overall Affect | 15% | Composite variability across all biometric channels |

**Risk bands:** `0.0–0.3` = Low &nbsp;|&nbsp; `0.3–0.6` = Moderate &nbsp;|&nbsp; `0.6–1.0` = High

---

## Setup & Installation

### Prerequisites
- Android Studio Hedgehog or later
- Android device or emulator with API 24+
- Camera permission
- Microphone permission

### Steps
```bash
git clone https://github.com/your-org/neuronexus.git
cd neuronexus
```

1. Open in Android Studio
2. Place the following model files in `app/src/main/assets/`:
   - `face_landmarker.task` — MediaPipe Face Landmarker model
   - `wav2vec_speech.onnx` — Acoustic feature model
   - `bert_linguistic.onnx` — Linguistic feature model
   - `fusion_classifier.onnx` — Multimodal fusion model
   - `neuronexus_llm.gguf` — Quantized LLM for insight generation
3. Sync Gradle and build

### Permissions
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

No network permission is declared or required.

---

## Privacy

- **No data ever leaves the device.** There are no network calls in the production app.
- No cloud storage, no telemetry, no analytics SDKs.
- All model inference runs locally via ONNX Runtime and Llama.cpp.
- Session data is stored encrypted in a local Room database.
- Users can delete all their data from Settings at any time.

---

## Clinical Validation Status

| Milestone | Status |
|---|---|
| Facial biometric pipeline | ✅ Built |
| Voice feature extraction | ✅ Built |
| Cognitive task engine | ✅ Built |
| Multimodal fusion model | ✅ Built |
| Read-aloud assistant | ✅ Built |
| ADReSS benchmark evaluation | 🔄 In progress |
| IRB approval for clinical data | 🔜 Planned |
| Neurology OPD pilot (100 patients) | 🔜 Planned |
| Android beta (500 users) | 🔜 Planned |

Target benchmark: ADReSS Challenge baseline ~82% accuracy. NeuroNexus targets to match and exceed via multimodal fusion advantage.
---


## Contact

**NeuroNexus Team**
Built for early dementia detection at scale.

*Detect early. Intervene early. Give people their time back.*
