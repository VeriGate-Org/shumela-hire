'use client';

import { useState, useEffect, useCallback } from 'react';
import {
  PlusIcon,
  PencilSquareIcon,
  TrashIcon,
} from '@heroicons/react/24/outline';
import { useToast } from '@/components/Toast';
import type { CustomField, CustomFieldForm, CustomFieldEntityType, CustomFieldDataType } from '@/types/employee';
import { getCustomFields, createCustomField, updateCustomField, deleteCustomField } from '@/services/employeeService';

const ENTITY_TYPE_LABELS: Record<CustomFieldEntityType, string> = {
  EMPLOYEE: 'Employee',
  EMPLOYEE_DOCUMENT: 'Document',
  EMPLOYMENT_EVENT: 'Employment Event',
};

const DATA_TYPE_LABELS: Record<CustomFieldDataType, string> = {
  TEXT: 'Text',
  NUMBER: 'Number',
  DATE: 'Date',
  BOOLEAN: 'Yes/No',
  SELECT: 'Single Select',
  MULTI_SELECT: 'Multi Select',
};

const EMPTY_FORM: CustomFieldForm = {
  fieldName: '', fieldLabel: '', entityType: '', dataType: '',
  isRequired: false, displayOrder: 0, options: '', defaultValue: '',
  validationRegex: '', helpText: '',
};

export default function CustomFieldBuilder() {
  const { toast } = useToast();
  const [fields, setFields] = useState<CustomField[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState<CustomFieldForm>(EMPTY_FORM);
  const [saving, setSaving] = useState(false);
  const [filterEntity, setFilterEntity] = useState('');

  const loadFields = useCallback(() => {
    setLoading(true);
    getCustomFields(filterEntity || undefined)
      .then(setFields)
      .catch(() => setFields([]))
      .finally(() => setLoading(false));
  }, [filterEntity]);

  useEffect(() => { loadFields(); }, [loadFields]);

  const openCreate = () => {
    setEditingId(null);
    setFormData(EMPTY_FORM);
    setShowForm(true);
  };

  const openEdit = (field: CustomField) => {
    setEditingId(field.id);
    setFormData({
      fieldName: field.fieldName,
      fieldLabel: field.fieldLabel,
      entityType: field.entityType,
      dataType: field.dataType,
      isRequired: field.isRequired,
      displayOrder: field.displayOrder,
      options: field.options || '',
      defaultValue: field.defaultValue || '',
      validationRegex: field.validationRegex || '',
      helpText: field.helpText || '',
    });
    setShowForm(true);
  };

  const handleSave = async () => {
    if (!formData.fieldName.trim() || !formData.fieldLabel.trim() || !formData.entityType || !formData.dataType) {
      toast('Please fill in all required fields', 'error');
      return;
    }
    setSaving(true);
    try {
      if (editingId) {
        await updateCustomField(editingId, formData);
        toast('Custom field updated', 'success');
      } else {
        await createCustomField(formData);
        toast('Custom field created', 'success');
      }
      setShowForm(false);
      setFormData(EMPTY_FORM);
      setEditingId(null);
      loadFields();
    } catch (err) {
      toast((err as Error).message || 'Failed to save', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number, label: string) => {
    if (!confirm(`Delete custom field "${label}"? This cannot be undone.`)) return;
    try {
      await deleteCustomField(id);
      toast('Custom field deleted', 'success');
      loadFields();
    } catch (err) {
      toast((err as Error).message || 'Delete failed', 'error');
    }
  };

  const set = (name: string, value: string | boolean | number) => {
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const inputCls = 'w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary';
  const labelCls = 'block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1';

  return (
    <div className="space-y-4">
      {/* Controls */}
      <div className="flex flex-col sm:flex-row gap-3 justify-between">
        <div className="flex gap-2">
          <select
            value={filterEntity}
            onChange={(e) => setFilterEntity(e.target.value)}
            className="px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
          >
            <option value="">All Entity Types</option>
            {(Object.keys(ENTITY_TYPE_LABELS) as CustomFieldEntityType[]).map(t => (
              <option key={t} value={t}>{ENTITY_TYPE_LABELS[t]}</option>
            ))}
          </select>
        </div>
        <button onClick={openCreate} className="btn-primary text-sm inline-flex items-center gap-1.5">
          <PlusIcon className="w-4 h-4" />
          Add Custom Field
        </button>
      </div>

      {/* Form */}
      {showForm && (
        <div className="enterprise-card p-4 space-y-3">
          <h3 className="text-sm font-semibold text-foreground">{editingId ? 'Edit' : 'New'} Custom Field</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
            <div>
              <label className={labelCls}>Field Name (key) *</label>
              <input type="text" value={formData.fieldName} onChange={e => set('fieldName', e.target.value)} className={inputCls} placeholder="e.g. shirt_size" />
            </div>
            <div>
              <label className={labelCls}>Display Label *</label>
              <input type="text" value={formData.fieldLabel} onChange={e => set('fieldLabel', e.target.value)} className={inputCls} placeholder="e.g. Shirt Size" />
            </div>
            <div>
              <label className={labelCls}>Entity Type *</label>
              <select value={formData.entityType} onChange={e => set('entityType', e.target.value)} className={inputCls}>
                <option value="">Select</option>
                {(Object.keys(ENTITY_TYPE_LABELS) as CustomFieldEntityType[]).map(t => (
                  <option key={t} value={t}>{ENTITY_TYPE_LABELS[t]}</option>
                ))}
              </select>
            </div>
            <div>
              <label className={labelCls}>Data Type *</label>
              <select value={formData.dataType} onChange={e => set('dataType', e.target.value)} className={inputCls}>
                <option value="">Select</option>
                {(Object.keys(DATA_TYPE_LABELS) as CustomFieldDataType[]).map(t => (
                  <option key={t} value={t}>{DATA_TYPE_LABELS[t]}</option>
                ))}
              </select>
            </div>
            <div>
              <label className={labelCls}>Display Order</label>
              <input type="number" value={formData.displayOrder} onChange={e => set('displayOrder', parseInt(e.target.value) || 0)} className={inputCls} />
            </div>
            <div>
              <label className={labelCls}>Default Value</label>
              <input type="text" value={formData.defaultValue} onChange={e => set('defaultValue', e.target.value)} className={inputCls} />
            </div>
            {(formData.dataType === 'SELECT' || formData.dataType === 'MULTI_SELECT') && (
              <div className="md:col-span-2 lg:col-span-3">
                <label className={labelCls}>Options (comma-separated)</label>
                <input type="text" value={formData.options} onChange={e => set('options', e.target.value)} className={inputCls} placeholder="Small, Medium, Large, XL" />
              </div>
            )}
            <div className="md:col-span-2 lg:col-span-3">
              <label className={labelCls}>Help Text</label>
              <input type="text" value={formData.helpText} onChange={e => set('helpText', e.target.value)} className={inputCls} placeholder="Guidance shown to users" />
            </div>
            <div>
              <label className={labelCls}>Validation Regex</label>
              <input type="text" value={formData.validationRegex} onChange={e => set('validationRegex', e.target.value)} className={inputCls} placeholder="Optional regex" />
            </div>
            <div className="flex items-end">
              <label className="inline-flex items-center gap-2 cursor-pointer">
                <input type="checkbox" checked={formData.isRequired} onChange={e => set('isRequired', e.target.checked)} className="rounded border-border text-primary focus:ring-primary/20" />
                <span className="text-sm text-foreground">Required field</span>
              </label>
            </div>
          </div>
          <div className="flex justify-end gap-2">
            <button onClick={() => { setShowForm(false); setEditingId(null); }} className="px-3 py-1.5 text-sm text-muted-foreground hover:text-foreground border border-border rounded-full">Cancel</button>
            <button onClick={handleSave} disabled={saving} className="btn-primary text-sm disabled:opacity-50">
              {saving ? 'Saving...' : editingId ? 'Update' : 'Create'}
            </button>
          </div>
        </div>
      )}

      {/* Fields list */}
      {loading ? (
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-cta" />
        </div>
      ) : fields.length === 0 ? (
        <div className="enterprise-card p-8 text-center">
          <p className="text-sm text-muted-foreground">No custom fields defined yet.</p>
        </div>
      ) : (
        <div className="enterprise-card overflow-hidden">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border">
                <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Label</th>
                <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Field Name</th>
                <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Entity</th>
                <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Type</th>
                <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Required</th>
                <th className="text-right text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {fields.map(f => (
                <tr key={f.id} className="hover:bg-accent/50 transition-colors">
                  <td className="px-4 py-3 text-sm font-medium text-foreground">{f.fieldLabel}</td>
                  <td className="px-4 py-3 text-sm text-muted-foreground font-mono">{f.fieldName}</td>
                  <td className="px-4 py-3 text-sm text-foreground">{ENTITY_TYPE_LABELS[f.entityType]}</td>
                  <td className="px-4 py-3 text-sm text-foreground">{DATA_TYPE_LABELS[f.dataType]}</td>
                  <td className="px-4 py-3 text-sm text-foreground">{f.isRequired ? 'Yes' : 'No'}</td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex justify-end gap-1">
                      <button onClick={() => openEdit(f)} className="p-1.5 text-muted-foreground hover:text-primary rounded-sm" aria-label="Edit">
                        <PencilSquareIcon className="w-4 h-4" />
                      </button>
                      <button onClick={() => handleDelete(f.id, f.fieldLabel)} className="p-1.5 text-muted-foreground hover:text-red-600 rounded-sm" aria-label="Delete">
                        <TrashIcon className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
