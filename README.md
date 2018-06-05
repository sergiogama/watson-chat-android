# Watson Chat Android

## Summary

This app uses Watson services to create a voice-activated chatbot. Requires a Bluemix account with the following services:

- Speech-to-Text
- Conversation
- Text-to-Speech

Optionally, custom replacements for each of these services may be used by adding them in the Settings. If that is the case, the app will still expect the same interface used in Watson services. Refer to the documentation for details:

- [Speech-to-Text Documentation](https://www.ibm.com/watson/developercloud/speech-to-text.html)
- [Conversation Documentation](https://www.ibm.com/watson/developercloud/conversation.html)
- [Text-to-Speech Documentation](https://www.ibm.com/watson/developercloud/text-to-speech.html)

## Speech Recognition

This module transcribes audio from the microphone in order to further process user input. By default, uses native recognition. Go to Settings -> Speech Recognition to configure this module. Customizations include:

- HTTP authentication
- Speech Recognition technology
- Input language

## Orchestration

This module understands user input in order to provide and answer. By default, uses Watson Conversation. Go to Settings -> Orchestration to configure this module. Customizations include:

- HTTP authentication
- Orchestration type

## Voice Synthesis

This module converts text into natural-sounding speech. By default, uses Watson Text-to-Speech. Go to Settings -> Voice Synthesis to configure this module. Customizations include:

- HTTP authentication
- Voice Synthesis technology
- Output language

## To do

- Appropriate enabling/disabling of configuration fields
- Watson Speech-to-Text support
- Native Voice Synthesis support
- Editable request/response body for custom services
