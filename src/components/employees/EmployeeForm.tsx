'use client';

import { useState } from 'react';
import { useToast } from '@/components/Toast';
import type { Employee, EmployeeCreateForm } from '@/types/employee';
import { createEmployee, updateEmployee } from '@/services/employeeService';

const EMPTY_FORM: EmployeeCreateForm = {
  firstName: '', lastName: '', title: '', preferredName: '',
  email: '', personalEmail: '', phone: '', mobilePhone: '',
  dateOfBirth: '', gender: '', race: '', disabilityStatus: '',
  citizenshipStatus: '', nationality: '', maritalStatus: '',
  idNumber: '', taxNumber: '', bankAccountNumber: '', bankName: '', bankBranchCode: '',
  physicalAddress: '', postalAddress: '', city: '', province: '', postalCode: '', country: '',
  department: '', division: '', jobTitle: '', jobGrade: '', employmentType: '',
  hireDate: '', probationEndDate: '', contractEndDate: '',
  reportingManagerId: '', costCentre: '', location: '', site: '',
  emergencyContactName: '', emergencyContactPhone: '', emergencyContactRelationship: '',
  demographicsConsent: false,
};

const SECTIONS = ['Personal', 'Contact & Address', 'Employment', 'Banking', 'Emergency Contact'] as const;
type Section = typeof SECTIONS[number];

interface EmployeeFormProps {
  employee?: Employee;
  onSaved: (emp: Employee) => void;
  onCancel: () => void;
}

function employeeToForm(emp: Employee): EmployeeCreateForm {
  return {
    firstName: emp.firstName ?? '', lastName: emp.lastName ?? '',
    title: emp.title ?? '', preferredName: emp.preferredName ?? '',
    email: emp.email ?? '', personalEmail: emp.personalEmail ?? '',
    phone: emp.phone ?? '', mobilePhone: emp.mobilePhone ?? '',
    dateOfBirth: emp.dateOfBirth ?? '', gender: emp.gender ?? '',
    race: emp.race ?? '', disabilityStatus: emp.disabilityStatus ?? '',
    citizenshipStatus: emp.citizenshipStatus ?? '', nationality: emp.nationality ?? '',
    maritalStatus: emp.maritalStatus ?? '',
    idNumber: '', taxNumber: '', bankAccountNumber: '',
    bankName: emp.bankName ?? '', bankBranchCode: emp.bankBranchCode ?? '',
    physicalAddress: emp.physicalAddress ?? '', postalAddress: emp.postalAddress ?? '',
    city: emp.city ?? '', province: emp.province ?? '',
    postalCode: emp.postalCode ?? '', country: emp.country ?? '',
    department: emp.department ?? '', division: emp.division ?? '',
    jobTitle: emp.jobTitle ?? '', jobGrade: emp.jobGrade ?? '',
    employmentType: emp.employmentType ?? '',
    hireDate: emp.hireDate ?? '', probationEndDate: emp.probationEndDate ?? '',
    contractEndDate: emp.contractEndDate ?? '',
    reportingManagerId: emp.reportingManagerId?.toString() ?? '',
    costCentre: emp.costCentre ?? '', location: emp.location ?? '', site: emp.site ?? '',
    emergencyContactName: emp.emergencyContactName ?? '',
    emergencyContactPhone: emp.emergencyContactPhone ?? '',
    emergencyContactRelationship: emp.emergencyContactRelationship ?? '',
    demographicsConsent: false,
  };
}

export default function EmployeeForm({ employee, onSaved, onCancel }: EmployeeFormProps) {
  const isEdit = !!employee;
  const { toast } = useToast();
  const [formData, setFormData] = useState<EmployeeCreateForm>(
    employee ? employeeToForm(employee) : EMPTY_FORM,
  );
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);
  const [activeSection, setActiveSection] = useState<Section>('Personal');

  const set = (name: string, value: string | boolean) => {
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => { const n = { ...prev }; delete n[name]; return n; });
  };

  const validate = (): boolean => {
    const e: Record<string, string> = {};
    if (!formData.firstName.trim()) e.firstName = 'First name is required';
    if (!formData.lastName.trim()) e.lastName = 'Last name is required';
    if (!formData.email.trim()) e.email = 'Email is required';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) e.email = 'Valid email is required';
    if (!formData.hireDate) e.hireDate = 'Hire date is required';
    setErrors(e);
    if (Object.keys(e).length > 0) {
      // Jump to first section with error
      if (e.firstName || e.lastName) setActiveSection('Personal');
      else if (e.email) setActiveSection('Contact & Address');
      else if (e.hireDate) setActiveSection('Employment');
    }
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    setSaving(true);
    try {
      const result = isEdit
        ? await updateEmployee(employee!.id, formData)
        : await createEmployee(formData);
      toast(isEdit ? 'Employee updated' : 'Employee created', 'success');
      onSaved(result);
    } catch (err) {
      toast((err as Error).message || 'Failed to save employee', 'error');
    } finally {
      setSaving(false);
    }
  };

  const inputCls = (field: string) =>
    `w-full px-3 py-2 border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary ${
      errors[field] ? 'border-red-400' : 'border-border'
    }`;

  const labelCls = 'block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1';

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Section tabs */}
      <div className="flex gap-1 overflow-x-auto border-b border-border pb-px">
        {SECTIONS.map(s => (
          <button
            key={s}
            type="button"
            onClick={() => setActiveSection(s)}
            className={`px-4 py-2 text-sm font-medium whitespace-nowrap border-b-2 -mb-px transition-colors ${
              activeSection === s
                ? 'border-primary text-primary'
                : 'border-transparent text-muted-foreground hover:text-foreground'
            }`}
          >
            {s}
          </button>
        ))}
      </div>

      {/* Personal */}
      {activeSection === 'Personal' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className={labelCls}>Title</label>
            <select value={formData.title} onChange={e => set('title', e.target.value)} className={inputCls('title')}>
              <option value="">Select</option>
              {['Mr', 'Mrs', 'Ms', 'Dr', 'Prof'].map(t => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls}>First Name *</label>
            <input type="text" value={formData.firstName} onChange={e => set('firstName', e.target.value)} className={inputCls('firstName')} />
            {errors.firstName && <p className="mt-1 text-xs text-red-600">{errors.firstName}</p>}
          </div>
          <div>
            <label className={labelCls}>Last Name *</label>
            <input type="text" value={formData.lastName} onChange={e => set('lastName', e.target.value)} className={inputCls('lastName')} />
            {errors.lastName && <p className="mt-1 text-xs text-red-600">{errors.lastName}</p>}
          </div>
          <div>
            <label className={labelCls}>Preferred Name</label>
            <input type="text" value={formData.preferredName} onChange={e => set('preferredName', e.target.value)} className={inputCls('preferredName')} />
          </div>
          <div>
            <label className={labelCls}>Date of Birth</label>
            <input type="date" value={formData.dateOfBirth} onChange={e => set('dateOfBirth', e.target.value)} className={inputCls('dateOfBirth')} />
          </div>
          <div>
            <label className={labelCls}>Gender</label>
            <select value={formData.gender} onChange={e => set('gender', e.target.value)} className={inputCls('gender')}>
              <option value="">Select</option>
              {['Male', 'Female', 'Non-Binary', 'Prefer not to say'].map(g => <option key={g} value={g}>{g}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls}>Race</label>
            <select value={formData.race} onChange={e => set('race', e.target.value)} className={inputCls('race')}>
              <option value="">Select</option>
              {['African', 'Coloured', 'Indian', 'White', 'Other'].map(r => <option key={r} value={r}>{r}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls}>Nationality</label>
            <input type="text" value={formData.nationality} onChange={e => set('nationality', e.target.value)} className={inputCls('nationality')} placeholder="e.g. South African" />
          </div>
          <div>
            <label className={labelCls}>Citizenship Status</label>
            <select value={formData.citizenshipStatus} onChange={e => set('citizenshipStatus', e.target.value)} className={inputCls('citizenshipStatus')}>
              <option value="">Select</option>
              {['Citizen', 'Permanent Resident', 'Work Permit', 'Other'].map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls}>Marital Status</label>
            <select value={formData.maritalStatus} onChange={e => set('maritalStatus', e.target.value)} className={inputCls('maritalStatus')}>
              <option value="">Select</option>
              {['Single', 'Married', 'Divorced', 'Widowed', 'Domestic Partnership'].map(m => <option key={m} value={m}>{m}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls}>Disability Status</label>
            <select value={formData.disabilityStatus} onChange={e => set('disabilityStatus', e.target.value)} className={inputCls('disabilityStatus')}>
              <option value="">Select</option>
              {['None', 'Visual', 'Hearing', 'Physical', 'Intellectual', 'Other'].map(d => <option key={d} value={d}>{d}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls}>SA ID Number</label>
            <input type="text" value={formData.idNumber} onChange={e => set('idNumber', e.target.value)} className={inputCls('idNumber')} placeholder="13 digits" />
          </div>
          <div>
            <label className={labelCls}>Tax Number</label>
            <input type="text" value={formData.taxNumber} onChange={e => set('taxNumber', e.target.value)} className={inputCls('taxNumber')} />
          </div>
          <div className="md:col-span-2 lg:col-span-3">
            <label className="inline-flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.demographicsConsent}
                onChange={e => set('demographicsConsent', e.target.checked)}
                className="rounded border-border text-primary focus:ring-primary/20"
              />
              <span className="text-sm text-foreground">
                Employee consents to demographic data processing (POPIA)
              </span>
            </label>
          </div>
        </div>
      )}

      {/* Contact & Address */}
      {activeSection === 'Contact & Address' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className={labelCls}>Work Email *</label>
            <input type="email" value={formData.email} onChange={e => set('email', e.target.value)} className={inputCls('email')} />
            {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email}</p>}
          </div>
          <div>
            <label className={labelCls}>Personal Email</label>
            <input type="email" value={formData.personalEmail} onChange={e => set('personalEmail', e.target.value)} className={inputCls('personalEmail')} />
          </div>
          <div>
            <label className={labelCls}>Phone</label>
            <input type="tel" value={formData.phone} onChange={e => set('phone', e.target.value)} className={inputCls('phone')} />
          </div>
          <div>
            <label className={labelCls}>Mobile Phone</label>
            <input type="tel" value={formData.mobilePhone} onChange={e => set('mobilePhone', e.target.value)} className={inputCls('mobilePhone')} />
          </div>
          <div className="md:col-span-2 lg:col-span-3">
            <label className={labelCls}>Physical Address</label>
            <textarea value={formData.physicalAddress} onChange={e => set('physicalAddress', e.target.value)} rows={2} className={inputCls('physicalAddress')} />
          </div>
          <div className="md:col-span-2 lg:col-span-3">
            <label className={labelCls}>Postal Address</label>
            <textarea value={formData.postalAddress} onChange={e => set('postalAddress', e.target.value)} rows={2} className={inputCls('postalAddress')} />
          </div>
          <div>
            <label className={labelCls}>City</label>
            <input type="text" value={formData.city} onChange={e => set('city', e.target.value)} className={inputCls('city')} />
          </div>
          <div>
            <label className={labelCls}>Province</label>
            <select value={formData.province} onChange={e => set('province', e.target.value)} className={inputCls('province')}>
              <option value="">Select</option>
              {['Eastern Cape', 'Free State', 'Gauteng', 'KwaZulu-Natal', 'Limpopo', 'Mpumalanga', 'North West', 'Northern Cape', 'Western Cape'].map(p => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
          </div>
          <div>
            <label className={labelCls}>Postal Code</label>
            <input type="text" value={formData.postalCode} onChange={e => set('postalCode', e.target.value)} className={inputCls('postalCode')} />
          </div>
          <div>
            <label className={labelCls}>Country</label>
            <input type="text" value={formData.country} onChange={e => set('country', e.target.value)} className={inputCls('country')} placeholder="South Africa" />
          </div>
        </div>
      )}

      {/* Employment */}
      {activeSection === 'Employment' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className={labelCls}>Department</label>
            <input type="text" value={formData.department} onChange={e => set('department', e.target.value)} className={inputCls('department')} />
          </div>
          <div>
            <label className={labelCls}>Division</label>
            <input type="text" value={formData.division} onChange={e => set('division', e.target.value)} className={inputCls('division')} />
          </div>
          <div>
            <label className={labelCls}>Job Title</label>
            <input type="text" value={formData.jobTitle} onChange={e => set('jobTitle', e.target.value)} className={inputCls('jobTitle')} />
          </div>
          <div>
            <label className={labelCls}>Job Grade</label>
            <input type="text" value={formData.jobGrade} onChange={e => set('jobGrade', e.target.value)} className={inputCls('jobGrade')} />
          </div>
          <div>
            <label className={labelCls}>Employment Type</label>
            <select value={formData.employmentType} onChange={e => set('employmentType', e.target.value)} className={inputCls('employmentType')}>
              <option value="">Select</option>
              {['Permanent', 'Fixed-Term Contract', 'Part-Time', 'Temporary', 'Intern', 'Learnership'].map(t => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </div>
          <div>
            <label className={labelCls}>Hire Date *</label>
            <input type="date" value={formData.hireDate} onChange={e => set('hireDate', e.target.value)} className={inputCls('hireDate')} />
            {errors.hireDate && <p className="mt-1 text-xs text-red-600">{errors.hireDate}</p>}
          </div>
          <div>
            <label className={labelCls}>Probation End Date</label>
            <input type="date" value={formData.probationEndDate} onChange={e => set('probationEndDate', e.target.value)} className={inputCls('probationEndDate')} />
          </div>
          <div>
            <label className={labelCls}>Contract End Date</label>
            <input type="date" value={formData.contractEndDate} onChange={e => set('contractEndDate', e.target.value)} className={inputCls('contractEndDate')} />
          </div>
          <div>
            <label className={labelCls}>Reporting Manager ID</label>
            <input type="number" value={formData.reportingManagerId} onChange={e => set('reportingManagerId', e.target.value)} className={inputCls('reportingManagerId')} />
          </div>
          <div>
            <label className={labelCls}>Cost Centre</label>
            <input type="text" value={formData.costCentre} onChange={e => set('costCentre', e.target.value)} className={inputCls('costCentre')} />
          </div>
          <div>
            <label className={labelCls}>Location</label>
            <input type="text" value={formData.location} onChange={e => set('location', e.target.value)} className={inputCls('location')} />
          </div>
          <div>
            <label className={labelCls}>Site</label>
            <input type="text" value={formData.site} onChange={e => set('site', e.target.value)} className={inputCls('site')} />
          </div>
        </div>
      )}

      {/* Banking */}
      {activeSection === 'Banking' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className={labelCls}>Bank Name</label>
            <select value={formData.bankName} onChange={e => set('bankName', e.target.value)} className={inputCls('bankName')}>
              <option value="">Select</option>
              {['ABSA', 'Capitec', 'FNB', 'Investec', 'Nedbank', 'Standard Bank', 'TymeBank', 'African Bank', 'Other'].map(b => (
                <option key={b} value={b}>{b}</option>
              ))}
            </select>
          </div>
          <div>
            <label className={labelCls}>Branch Code</label>
            <input type="text" value={formData.bankBranchCode} onChange={e => set('bankBranchCode', e.target.value)} className={inputCls('bankBranchCode')} />
          </div>
          <div>
            <label className={labelCls}>Account Number</label>
            <input type="text" value={formData.bankAccountNumber} onChange={e => set('bankAccountNumber', e.target.value)} className={inputCls('bankAccountNumber')} />
          </div>
        </div>
      )}

      {/* Emergency Contact */}
      {activeSection === 'Emergency Contact' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className={labelCls}>Contact Name</label>
            <input type="text" value={formData.emergencyContactName} onChange={e => set('emergencyContactName', e.target.value)} className={inputCls('emergencyContactName')} />
          </div>
          <div>
            <label className={labelCls}>Contact Phone</label>
            <input type="tel" value={formData.emergencyContactPhone} onChange={e => set('emergencyContactPhone', e.target.value)} className={inputCls('emergencyContactPhone')} />
          </div>
          <div>
            <label className={labelCls}>Relationship</label>
            <select value={formData.emergencyContactRelationship} onChange={e => set('emergencyContactRelationship', e.target.value)} className={inputCls('emergencyContactRelationship')}>
              <option value="">Select</option>
              {['Spouse', 'Parent', 'Sibling', 'Child', 'Friend', 'Other'].map(r => (
                <option key={r} value={r}>{r}</option>
              ))}
            </select>
          </div>
        </div>
      )}

      {/* Actions */}
      <div className="flex items-center justify-end gap-3 pt-4 border-t border-border">
        <button type="button" onClick={onCancel} className="px-4 py-2 text-sm font-medium text-muted-foreground hover:text-foreground border border-border rounded-full">
          Cancel
        </button>
        <button type="submit" disabled={saving} className="btn-primary text-sm disabled:opacity-50">
          {saving ? 'Saving...' : isEdit ? 'Update Employee' : 'Create Employee'}
        </button>
      </div>
    </form>
  );
}
