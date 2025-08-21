// Export all job template components
export { default as TemplateList } from './TemplateList';
export { default as TemplateEditor } from './TemplateEditor';
export { default as GenerateFromTemplate } from './GenerateFromTemplate';

// Export types for convenience
export type {
  JobAdTemplate,
  JobAdDraft,
  TemplatePlaceholder,
  GenerateAdRequest,
  TemplateFilters,
  TemplateStats
} from '../../types/jobTemplate';

// Export service for direct usage
export { jobTemplateService } from '../../services/jobTemplateService';

// Export constants
export { 
  TEMPLATE_PLACEHOLDERS, 
  DEFAULT_TEMPLATE_CONTENT 
} from '../../types/jobTemplate';