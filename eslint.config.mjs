// For more info, see https://github.com/storybookjs/eslint-plugin-storybook#configuration-flat-config-format
import storybook from "eslint-plugin-storybook";

import { dirname } from "path";
import { fileURLToPath } from "url";
import { FlatCompat } from "@eslint/eslintrc";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const compat = new FlatCompat({
  baseDirectory: __dirname,
});

const eslintConfig = [
  ...compat.extends("next/core-web-vitals", "next/typescript"),
  ...storybook.configs["flat/recommended"],
  {
    rules: {
      // Allow explicit any — the API layer and service code use any intentionally
      "@typescript-eslint/no-explicit-any": "off",
      // Warn on unused variables — does not block build
      "@typescript-eslint/no-unused-vars": ["warn", { argsIgnorePattern: "^_", varsIgnorePattern: "^_" }],
      // Enforce hooks dependency arrays
      "react-hooks/exhaustive-deps": "warn",
      // Enforce escaped entities in JSX
      "react/no-unescaped-entities": "error",
      // Allow img elements — Next.js Image is not always appropriate
      "@next/next/no-img-element": "off",
      // Enforce const for never-reassigned variables
      "prefer-const": "error",
      // Allow storybook renderer imports — pending migration to framework packages
      "storybook/no-renderer-packages": "off",
      // Enforce hooks rules
      "react-hooks/rules-of-hooks": "error",
    },
  },
];

export default eslintConfig;
