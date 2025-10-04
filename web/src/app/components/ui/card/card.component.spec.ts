import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CardComponent, CardVariant, CardSize } from './card.component';
import { LoadingComponent } from '../loading/loading.component';

describe('CardComponent', () => {
  let component: CardComponent;
  let fixture: ComponentFixture<CardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardComponent, LoadingComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display title when provided', () => {
    component.title = 'Test Title';
    component.showHeader = true;
    fixture.detectChanges();

    const titleElement = fixture.nativeElement.querySelector('.card-title');
    expect(titleElement).toBeTruthy();
    expect(titleElement.textContent.trim()).toBe('Test Title');
  });

  it('should display subtitle when provided', () => {
    component.subtitle = 'Test Subtitle';
    component.showHeader = true;
    fixture.detectChanges();

    const subtitleElement = fixture.nativeElement.querySelector('.card-subtitle');
    expect(subtitleElement).toBeTruthy();
    expect(subtitleElement.textContent.trim()).toBe('Test Subtitle');
  });

  it('should not show header when showHeader is false', () => {
    component.title = 'Test Title';
    component.showHeader = false;
    fixture.detectChanges();

    const headerElement = fixture.nativeElement.querySelector('.card-header');
    expect(headerElement).toBeFalsy();
  });

  it('should not show header when no title or subtitle', () => {
    component.showHeader = true;
    fixture.detectChanges();

    const headerElement = fixture.nativeElement.querySelector('.card-header');
    expect(headerElement).toBeFalsy();
  });

  it('should show footer when showFooter is true', () => {
    component.showFooter = true;
    fixture.detectChanges();

    const footerElement = fixture.nativeElement.querySelector('.card-footer');
    expect(footerElement).toBeTruthy();
  });

  it('should not show footer when showFooter is false', () => {
    component.showFooter = false;
    fixture.detectChanges();

    const footerElement = fixture.nativeElement.querySelector('.card-footer');
    expect(footerElement).toBeFalsy();
  });

  it('should apply correct variant classes', () => {
    const variants: CardVariant[] = ['default', 'elevated', 'outlined', 'flat'];

    variants.forEach(variant => {
      component.variant = variant;
      fixture.detectChanges();

      const cardElement = fixture.nativeElement.querySelector('div');
      expect(cardElement.classList.contains(`card-${variant}`)).toBe(true);
    });
  });

  it('should apply correct size classes', () => {
    const sizes: CardSize[] = ['small', 'medium', 'large'];

    sizes.forEach(size => {
      component.size = size;
      fixture.detectChanges();

      const cardElement = fixture.nativeElement.querySelector('div');
      expect(cardElement.classList.contains(`card-${size}`)).toBe(true);
    });
  });

  it('should apply clickable class when clickable is true', () => {
    component.clickable = true;
    fixture.detectChanges();

    const cardElement = fixture.nativeElement.querySelector('div');
    expect(cardElement.classList.contains('card-clickable')).toBe(true);
  });

  it('should apply hoverable class when hoverable is true', () => {
    component.hoverable = true;
    fixture.detectChanges();

    const cardElement = fixture.nativeElement.querySelector('div');
    expect(cardElement.classList.contains('card-hoverable')).toBe(true);
  });

  it('should apply loading class when loading is true', () => {
    component.loading = true;
    fixture.detectChanges();

    const cardElement = fixture.nativeElement.querySelector('div');
    expect(cardElement.classList.contains('card-loading')).toBe(true);
  });

  it('should show loading overlay when loading is true', () => {
    component.loading = true;
    fixture.detectChanges();

    const loadingOverlay = fixture.nativeElement.querySelector('.card-loading-overlay');
    expect(loadingOverlay).toBeTruthy();
  });

  it('should not show loading overlay when loading is false', () => {
    component.loading = false;
    fixture.detectChanges();

    const loadingOverlay = fixture.nativeElement.querySelector('.card-loading-overlay');
    expect(loadingOverlay).toBeFalsy();
  });

  it('should have correct default values', () => {
    expect(component.variant).toBe('default');
    expect(component.size).toBe('medium');
    expect(component.title).toBe('');
    expect(component.subtitle).toBe('');
    expect(component.showHeader).toBe(true);
    expect(component.showFooter).toBe(false);
    expect(component.clickable).toBe(false);
    expect(component.loading).toBe(false);
    expect(component.hoverable).toBe(false);
  });

  it('should combine multiple classes correctly', () => {
    component.variant = 'elevated';
    component.size = 'large';
    component.clickable = true;
    component.hoverable = true;
    component.loading = true;
    fixture.detectChanges();

    const cardElement = fixture.nativeElement.querySelector('div');
    expect(cardElement.classList.contains('card-elevated')).toBe(true);
    expect(cardElement.classList.contains('card-large')).toBe(true);
    expect(cardElement.classList.contains('card-clickable')).toBe(true);
    expect(cardElement.classList.contains('card-hoverable')).toBe(true);
    expect(cardElement.classList.contains('card-loading')).toBe(true);
  });
});
