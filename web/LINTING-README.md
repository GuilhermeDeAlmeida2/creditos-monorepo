# Configurações de Linting e Formatação

Este projeto está configurado com ESLint e Prettier para manter a qualidade e consistência do código.

## 🛠️ Ferramentas Configuradas

### ESLint
- **Configuração**: `.eslintrc.json`
- **Parser**: TypeScript ESLint Parser
- **Regras principais**:
  - `prefer-const`: Força uso de `const` quando possível
  - `no-var`: Proíbe uso de `var`
  - `no-console`: Avisa sobre uso de `console.log`
  - `no-debugger`: Proíbe uso de `debugger`

### Prettier
- **Configuração**: `.prettierrc`
- **Características**:
  - Aspas simples
  - Ponto e vírgula obrigatório
  - Trailing comma em ES5
  - Largura máxima de linha: 100 caracteres
  - Indentação: 2 espaços

### Husky + lint-staged
- **Pre-commit hooks**: Executa lint e formatação automaticamente
- **Arquivos processados**: `.ts`, `.html`, `.css`, `.scss`, `.json`

## 📋 Scripts Disponíveis

```bash
# Executar linting
npm run lint

# Executar linting com correção automática
npm run lint:fix

# Formatar código com Prettier
npm run format

# Verificar formatação sem alterar arquivos
npm run format:check

# Executar lint-staged (usado pelo Husky)
npm run lint-staged
```

## 🚀 Como Usar

### 1. Desenvolvimento Normal
Os hooks do Husky executarão automaticamente o lint e formatação antes de cada commit.

### 2. Formatação Manual
```bash
# Formatar todos os arquivos
npm run format

# Verificar se está formatado
npm run format:check
```

### 3. Linting Manual
```bash
# Verificar problemas
npm run lint

# Corrigir problemas automaticamente
npm run lint:fix
```

## ⚙️ Configuração do IDE

### VS Code
Recomenda-se instalar as seguintes extensões:
- **ESLint** (ms-vscode.vscode-eslint)
- **Prettier** (esbenp.prettier-vscode)

### Configuração do VS Code (.vscode/settings.json)
```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "eslint.validate": ["typescript"]
}
```

## 🔧 Personalização

### Adicionar Novas Regras ESLint
Edite o arquivo `.eslintrc.json` na seção `rules`:

```json
{
  "rules": {
    "nova-regra": "error"
  }
}
```

### Modificar Configuração Prettier
Edite o arquivo `.prettierrc`:

```json
{
  "semi": false,
  "singleQuote": false
}
```

## 📁 Arquivos de Configuração

- `.eslintrc.json` - Configuração do ESLint
- `.prettierrc` - Configuração do Prettier
- `.prettierignore` - Arquivos ignorados pelo Prettier
- `.eslintignore` - Arquivos ignorados pelo ESLint
- `.husky/pre-commit` - Hook de pre-commit
- `package.json` - Scripts e configuração do lint-staged

## 🐛 Solução de Problemas

### ESLint não encontra regras
```bash
npm install --save-dev @typescript-eslint/eslint-plugin @typescript-eslint/parser
```

### Prettier não formata arquivos
Verifique se os arquivos não estão no `.prettierignore`.

### Husky não executa
```bash
npx husky install
chmod +x .husky/pre-commit
```

## 📚 Recursos Adicionais

- [ESLint Documentation](https://eslint.org/)
- [Prettier Documentation](https://prettier.io/)
- [Husky Documentation](https://typicode.github.io/husky/)
- [lint-staged Documentation](https://github.com/okonet/lint-staged)
