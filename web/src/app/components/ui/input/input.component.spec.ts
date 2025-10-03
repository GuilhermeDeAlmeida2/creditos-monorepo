import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InputComponent, InputType, InputSize } from './input.component';
import { FormsModule } from '@angular/forms';

describe('InputComponent', () => {
  let component: InputComponent;
  let fixture: ComponentFixture<InputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InputComponent, FormsModule]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(InputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit valueChange when input changes', () => {
    spyOn(component.valueChange, 'emit');
    const inputElement = fixture.nativeElement.querySelector('input');
    inputElement.value = 'test value';
    inputElement.dispatchEvent(new Event('input'));
    
    expect(component.valueChange.emit).toHaveBeenCalledWith('test value');
  });

  it('should emit enterKey when Enter is pressed', () => {
    spyOn(component.enterKey, 'emit');
    const inputElement = fixture.nativeElement.querySelector('input');
    inputElement.dispatchEvent(new KeyboardEvent('keyup', { key: 'Enter' }));
    
    expect(component.enterKey.emit).toHaveBeenCalled();
  });

  it('should emit focusEvent when input is focused', () => {
    spyOn(component.focusEvent, 'emit');
    const inputElement = fixture.nativeElement.querySelector('input');
    inputElement.dispatchEvent(new Event('focus'));
    
    expect(component.focusEvent.emit).toHaveBeenCalled();
  });

  it('should emit blurEvent when input loses focus', () => {
    spyOn(component.blurEvent, 'emit');
    const inputElement = fixture.nativeElement.querySelector('input');
    inputElement.dispatchEvent(new Event('blur'));
    
    expect(component.blurEvent.emit).toHaveBeenCalled();
  });

  it('should clear value when clear button is clicked', () => {
    component.value = 'test value';
    component.showClearButton = true;
    fixture.detectChanges();
    
    spyOn(component.valueChange, 'emit');
    const clearButton = fixture.nativeElement.querySelector('.input-clear');
    clearButton.click();
    
    expect(component.value).toBe('');
    expect(component.valueChange.emit).toHaveBeenCalledWith('');
  });

  it('should apply correct validation state classes', () => {
    component.validationState = 'error';
    component.errorMessage = 'Error message';
    fixture.detectChanges();
    
    const container = fixture.nativeElement.querySelector('.input-container');
    expect(container.classList.contains('input-error')).toBe(true);
  });

  it('should apply correct size classes', () => {
    const sizes: InputSize[] = ['small', 'medium', 'large'];
    
    sizes.forEach(size => {
      component.size = size;
      fixture.detectChanges();
      
      const container = fixture.nativeElement.querySelector('.input-container');
      
      if (size === 'medium') {
        expect(container.classList.contains('input-medium')).toBe(false);
      } else {
        expect(container.classList.contains(`input-${size}`)).toBe(true);
      }
    });
  });

  it('should show label when provided', () => {
    component.label = 'Test Label';
    fixture.detectChanges();
    
    const label = fixture.nativeElement.querySelector('.input-label');
    expect(label).toBeTruthy();
    expect(label.textContent.trim()).toBe('Test Label');
  });

  it('should show required indicator when required', () => {
    component.label = 'Test Label';
    component.required = true;
    fixture.detectChanges();
    
    const requiredIndicator = fixture.nativeElement.querySelector('.required-indicator');
    expect(requiredIndicator).toBeTruthy();
    expect(requiredIndicator.textContent.trim()).toBe('*');
  });

  it('should show error message when validation state is error', () => {
    component.validationState = 'error';
    component.errorMessage = 'This field is required';
    fixture.detectChanges();
    
    const errorElement = fixture.nativeElement.querySelector('.input-error');
    expect(errorElement).toBeTruthy();
    expect(errorElement.textContent.trim()).toBe('This field is required');
  });

  it('should show hint when provided and no error', () => {
    component.hint = 'This is a hint';
    fixture.detectChanges();
    
    const hintElement = fixture.nativeElement.querySelector('.input-hint');
    expect(hintElement).toBeTruthy();
    expect(hintElement.textContent.trim()).toBe('This is a hint');
  });

  it('should not show hint when there is an error', () => {
    component.hint = 'This is a hint';
    component.validationState = 'error';
    component.errorMessage = 'Error message';
    fixture.detectChanges();
    
    const hintElement = fixture.nativeElement.querySelector('.input-hint');
    expect(hintElement).toBeFalsy();
  });

  it('should have correct default values', () => {
    expect(component.type).toBe('text');
    expect(component.size).toBe('medium');
    expect(component.disabled).toBe(false);
    expect(component.readonly).toBe(false);
    expect(component.required).toBe(false);
    expect(component.loading).toBe(false);
    expect(component.showClearButton).toBe(false);
    expect(component.validationState).toBe('none');
  });

  // ControlValueAccessor tests
  it('should implement ControlValueAccessor', () => {
    expect(component.writeValue).toBeDefined();
    expect(component.registerOnChange).toBeDefined();
    expect(component.registerOnTouched).toBeDefined();
    expect(component.setDisabledState).toBeDefined();
  });

  it('should write value correctly', () => {
    component.writeValue('test value');
    expect(component.value).toBe('test value');
  });

  it('should set disabled state correctly', () => {
    component.setDisabledState(true);
    expect(component.disabled).toBe(true);
  });
});
