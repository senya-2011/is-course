module.exports = {
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'eslint:recommended'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  rules: {
    // Основные правила
    'indent': ['error', 4],
    'linebreak-style': 'off',
    'quotes': ['error', 'single'],
    'semi': ['error', 'always'],
    
    // Переменные
    'no-unused-vars': 'warn',
    'no-undef': 'error',
    
    // Функции
    'no-empty': 'warn',
    'no-console': 'off',
    
    // Стиль кода
    'comma-dangle': ['error', 'never'],
    'eol-last': ['error', 'always'],
    'no-trailing-spaces': 'error',
    'object-curly-spacing': ['error', 'always'],
    'array-bracket-spacing': ['error', 'never'],
    
    // Безопасность
    'no-eval': 'error',
    'no-implied-eval': 'error',
    'no-new-func': 'error'
  },
  globals: {
    'EventSource': 'readonly',
    'window': 'readonly',
    'location': 'readonly'
  }
};
