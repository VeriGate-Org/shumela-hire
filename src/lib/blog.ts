import fs from 'fs';
import path from 'path';

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

export interface BlogPost {
  slug: string;
  title: string;
  description: string;
  date: string;
  author: string;
  category: string;
  readTime: string;
  content: string;
}

/* ------------------------------------------------------------------ */
/*  Constants                                                          */
/* ------------------------------------------------------------------ */

const CONTENT_DIR = path.join(process.cwd(), 'src/content/blog');

/* ------------------------------------------------------------------ */
/*  Frontmatter parser                                                 */
/* ------------------------------------------------------------------ */

function parseFrontmatter(fileContent: string): {
  frontmatter: Record<string, string>;
  content: string;
} {
  const lines = fileContent.split('\n');

  if (lines[0]?.trim() !== '---') {
    return { frontmatter: {}, content: fileContent };
  }

  let closingIndex = -1;
  for (let i = 1; i < lines.length; i++) {
    if (lines[i]?.trim() === '---') {
      closingIndex = i;
      break;
    }
  }

  if (closingIndex === -1) {
    return { frontmatter: {}, content: fileContent };
  }

  const frontmatterLines = lines.slice(1, closingIndex);
  const frontmatter: Record<string, string> = {};

  for (const line of frontmatterLines) {
    const colonIndex = line.indexOf(':');
    if (colonIndex === -1) continue;

    const key = line.slice(0, colonIndex).trim();
    let value = line.slice(colonIndex + 1).trim();

    // Strip surrounding quotes
    if (
      (value.startsWith('"') && value.endsWith('"')) ||
      (value.startsWith("'") && value.endsWith("'"))
    ) {
      value = value.slice(1, -1);
    }

    frontmatter[key] = value;
  }

  const content = lines.slice(closingIndex + 1).join('\n').trim();

  return { frontmatter, content };
}

/* ------------------------------------------------------------------ */
/*  Markdown to HTML converter                                         */
/* ------------------------------------------------------------------ */

export function renderMarkdown(markdown: string): string {
  const lines = markdown.split('\n');
  const htmlLines: string[] = [];
  let inList = false;
  let inOrderedList = false;
  let currentParagraph: string[] = [];

  function flushParagraph() {
    if (currentParagraph.length > 0) {
      const text = currentParagraph.join(' ');
      htmlLines.push(`<p>${inlineFormat(text)}</p>`);
      currentParagraph = [];
    }
  }

  function closeList() {
    if (inList) {
      htmlLines.push('</ul>');
      inList = false;
    }
    if (inOrderedList) {
      htmlLines.push('</ol>');
      inOrderedList = false;
    }
  }

  function inlineFormat(text: string): string {
    // Bold
    text = text.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
    // Italic
    text = text.replace(/\*(.+?)\*/g, '<em>$1</em>');
    // Inline code
    text = text.replace(/`(.+?)`/g, '<code>$1</code>');
    return text;
  }

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    const trimmed = line.trim();

    // Blank line
    if (trimmed === '') {
      flushParagraph();
      closeList();
      continue;
    }

    // Headings
    if (trimmed.startsWith('### ')) {
      flushParagraph();
      closeList();
      htmlLines.push(`<h3>${inlineFormat(trimmed.slice(4))}</h3>`);
      continue;
    }

    if (trimmed.startsWith('## ')) {
      flushParagraph();
      closeList();
      htmlLines.push(`<h2>${inlineFormat(trimmed.slice(3))}</h2>`);
      continue;
    }

    // Blockquote
    if (trimmed.startsWith('> ')) {
      flushParagraph();
      closeList();
      htmlLines.push(`<blockquote>${inlineFormat(trimmed.slice(2))}</blockquote>`);
      continue;
    }

    // Unordered list item
    if (trimmed.startsWith('- ')) {
      flushParagraph();
      if (inOrderedList) {
        htmlLines.push('</ol>');
        inOrderedList = false;
      }
      if (!inList) {
        htmlLines.push('<ul>');
        inList = true;
      }
      htmlLines.push(`<li>${inlineFormat(trimmed.slice(2))}</li>`);
      continue;
    }

    // Ordered list item
    const orderedMatch = trimmed.match(/^(\d+)\.\s+/);
    if (orderedMatch) {
      flushParagraph();
      if (inList) {
        htmlLines.push('</ul>');
        inList = false;
      }
      if (!inOrderedList) {
        htmlLines.push('<ol>');
        inOrderedList = true;
      }
      htmlLines.push(
        `<li>${inlineFormat(trimmed.slice(orderedMatch[0].length))}</li>`
      );
      continue;
    }

    // Regular text - accumulate into a paragraph
    currentParagraph.push(trimmed);
  }

  flushParagraph();
  closeList();

  return htmlLines.join('\n');
}

/* ------------------------------------------------------------------ */
/*  Public API                                                         */
/* ------------------------------------------------------------------ */

export function getAllPosts(): BlogPost[] {
  if (!fs.existsSync(CONTENT_DIR)) {
    return [];
  }

  const files = fs.readdirSync(CONTENT_DIR).filter((f) => f.endsWith('.mdx'));

  const posts: BlogPost[] = files.map((filename) => {
    const slug = filename.replace(/\.mdx$/, '');
    const filePath = path.join(CONTENT_DIR, filename);
    const fileContent = fs.readFileSync(filePath, 'utf-8');
    const { frontmatter, content } = parseFrontmatter(fileContent);

    return {
      slug,
      title: frontmatter.title ?? '',
      description: frontmatter.description ?? '',
      date: frontmatter.date ?? '',
      author: frontmatter.author ?? '',
      category: frontmatter.category ?? '',
      readTime: frontmatter.readTime ?? '',
      content,
    };
  });

  // Sort by date descending
  posts.sort((a, b) => (a.date > b.date ? -1 : a.date < b.date ? 1 : 0));

  return posts;
}

export function getPostBySlug(slug: string): BlogPost | null {
  const filePath = path.join(CONTENT_DIR, `${slug}.mdx`);

  if (!fs.existsSync(filePath)) {
    return null;
  }

  const fileContent = fs.readFileSync(filePath, 'utf-8');
  const { frontmatter, content } = parseFrontmatter(fileContent);

  return {
    slug,
    title: frontmatter.title ?? '',
    description: frontmatter.description ?? '',
    date: frontmatter.date ?? '',
    author: frontmatter.author ?? '',
    category: frontmatter.category ?? '',
    readTime: frontmatter.readTime ?? '',
    content,
  };
}

export function getPostSlugs(): string[] {
  if (!fs.existsSync(CONTENT_DIR)) {
    return [];
  }

  return fs
    .readdirSync(CONTENT_DIR)
    .filter((f) => f.endsWith('.mdx'))
    .map((f) => f.replace(/\.mdx$/, ''));
}
