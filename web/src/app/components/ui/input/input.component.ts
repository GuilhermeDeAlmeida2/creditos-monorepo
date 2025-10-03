import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

export type InputType = 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'search';
export type InputSize = 'small' | 'medium' | 'large';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputComponent),
      multi: true
    }
  ]
})
export class InputComponent implements ControlValueAccessor {
  @Input() type: InputType = 'text';
  @Input() size: InputSize = 'medium';
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() hint: string = '';
  @Input() disabled: boolean = false;
  @Input() readonly: boolean = false;
  @Input() required: boolean = false;
  @Input() loading: boolean = false;
  @Input() showClearButton: boolean = false;
  @Input() errorMessage: string = '';
  @Input() validationState: 'success' | 'error' | 'none' = 'none';

  @Output() valueChange = new EventEmitter<string>();
  @Output() enterKey = new EventEmitter<void>();
  @Output() focusEvent = new EventEmitter<void>();
  @Output() blurEvent = new EventEmitter<void>();

  value: string = '';
  inputId: string = `input-${Math.random().toString(36).substr(2, 9)}`;
  errorId: string = `error-${this.inputId}`;

  private onChange = (value: string) => {};
  private onTouched = () => {};

  get hasError(): boolean {
    return this.validationState === 'error' && !!this.errorMessage;
  }

  getContainerClasses(): string {
    const classes = ['input-container'];
    
    if (this.size !== 'medium') {
      classes.push(`input-${this.size}`);
    }
    
    if (this.validationState === 'error') {
      classes.push('input-error');
    } else if (this.validationState === 'success') {
      classes.push('input-success');
    }
    
    return classes.join(' ');
  }

  getInputClasses(): string {
    return '';
  }

  onInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.value = target.value;
    this.onChange(this.value);
    this.valueChange.emit(this.value);
  }

  onBlur(): void {
    this.onTouched();
    this.blurEvent.emit();
  }

  onFocus(): void {
    this.focusEvent.emit();
  }

  onEnterKey(): void {
    this.enterKey.emit();
  }

  clearValue(): void {
    this.value = '';
    this.onChange(this.value);
    this.valueChange.emit(this.value);
  }

  // ControlValueAccessor implementation
  writeValue(value: string): void {
    this.value = value || '';
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
}
