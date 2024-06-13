import globals from "globals";
import pluginJs from "@eslint/js";


export default [
  {
    files: ["**/*.js"],
    languageOptions: {
      // sourceType: "script"
      sourceType: "module",
      globals: globals.browser,
    }
  },
  // {languageOptions: { globals: globals.browser }},
  pluginJs.configs.recommended,
  {
    rules: {
      'no-unused-vars': 'warn', // 將未使用變數設定為警告
    }
  }
];