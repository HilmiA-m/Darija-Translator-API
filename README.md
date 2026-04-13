# Darija Translator API

RESTful API that translates English to Moroccan Arabic (Darija) using Google Gemini.

## Features
- POST /api/translate - Translate English text to Darija
- Basic Authentication
- Chrome Extension included

## Test
```bash
curl -X POST http://localhost:8080/TranslatorAPI/api/translate \
  -u translator:darija123 \
  -d "text=Hello"
cd ~/TranslatorAPI
cat > README.md << 'EOF'
# Darija Translator API

RESTful API that translates English to Moroccan Arabic (Darija) using Google Gemini.

## Features
- POST /api/translate - Translate English text to Darija
- Basic Authentication
- Chrome Extension included

## Test
curl -X POST http://localhost:8080/TranslatorAPI/api/translate -u translator:darija123 -d "text=Hello"
