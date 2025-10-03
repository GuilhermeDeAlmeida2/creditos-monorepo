import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ButtonComponent, ButtonVariant, ButtonSize } from './button.component';

describe('ButtonComponent', () => {
  let component: ButtonComponent;
  let fixture: ComponentFixture<ButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ButtonComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit clicked event when clicked', () => {
    spyOn(component.clicked, 'emit');
    component.onClick();
    expect(component.clicked.emit).toHaveBeenCalled();
  });

  it('should not emit clicked event when disabled', () => {
    component.disabled = true;
    spyOn(component.clicked, 'emit');
    component.onClick();
    expect(component.clicked.emit).not.toHaveBeenCalled();
  });

  it('should not emit clicked event when loading', () => {
    component.loading = true;
    spyOn(component.clicked, 'emit');
    component.onClick();
    expect(component.clicked.emit).not.toHaveBeenCalled();
  });

  it('should apply correct variant class', () => {
    const variants: ButtonVariant[] = [
      'primary',
      'secondary',
      'success',
      'error',
      'info',
      'warning',
    ];

    variants.forEach(variant => {
      component.variant = variant;
      const classes = component.getButtonClasses();
      expect(classes).toContain(`btn-${variant}`);
    });
  });

  it('should apply correct size class', () => {
    const sizes: ButtonSize[] = ['small', 'medium', 'large'];

    sizes.forEach(size => {
      component.size = size;
      const classes = component.getButtonClasses();

      if (size === 'medium') {
        expect(classes).not.toContain('btn-medium');
      } else {
        expect(classes).toContain(`btn-${size}`);
      }
    });
  });

  it('should have default values', () => {
    expect(component.variant).toBe('primary');
    expect(component.size).toBe('medium');
    expect(component.disabled).toBe(false);
    expect(component.loading).toBe(false);
    expect(component.type).toBe('button');
    expect(component.fullWidth).toBe(false);
  });
});
