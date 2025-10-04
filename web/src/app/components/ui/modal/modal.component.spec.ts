import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ModalComponent, ModalButton } from './modal.component';

describe('ModalComponent', () => {
  let component: ModalComponent;
  let fixture: ComponentFixture<ModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not be visible when isOpen is false', () => {
    component.isOpen = false;
    fixture.detectChanges();

    const modal = fixture.nativeElement.querySelector('.modal');
    expect(modal).toBeFalsy();
  });

  it('should be visible when isOpen is true', () => {
    component.isOpen = true;
    fixture.detectChanges();

    const modal = fixture.nativeElement.querySelector('.modal');
    expect(modal).toBeTruthy();
    expect(modal.classList.contains('modal-open')).toBeTruthy();
  });

  it('should display title when provided', () => {
    component.isOpen = true;
    component.title = 'Test Modal';
    fixture.detectChanges();

    const title = fixture.nativeElement.querySelector('.modal-title');
    expect(title.textContent).toContain('Test Modal');
  });

  it('should emit opened event when modal opens', () => {
    spyOn(component.opened, 'emit');

    component.openModal();

    expect(component.opened.emit).toHaveBeenCalled();
  });

  it('should emit closed event when modal closes', () => {
    spyOn(component.closed, 'emit');

    component.closeModal();

    expect(component.closed.emit).toHaveBeenCalled();
  });

  it('should close modal when close button is clicked', () => {
    component.isOpen = true;
    component.closable = true;
    component.showCloseButton = true;
    fixture.detectChanges();

    spyOn(component, 'closeModal');

    const closeButton = fixture.nativeElement.querySelector('.modal-close-btn');
    closeButton.click();

    expect(component.closeModal).toHaveBeenCalled();
  });

  it('should execute button action when button is clicked', () => {
    const mockButton: ModalButton = {
      label: 'Save',
      variant: 'primary',
      onClick: jasmine.createSpy('onClick'),
    };

    component.isOpen = true;
    component.buttons = [mockButton];
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('.modal-btn');
    button.click();

    expect(mockButton.onClick).toHaveBeenCalled();
  });

  it('should not execute button action when button is disabled', () => {
    const mockButton: ModalButton = {
      label: 'Save',
      variant: 'primary',
      disabled: true,
      onClick: jasmine.createSpy('onClick'),
    };

    component.isOpen = true;
    component.buttons = [mockButton];
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('.modal-btn');
    button.click();

    expect(mockButton.onClick).not.toHaveBeenCalled();
  });

  it('should show loading state when loading is true', () => {
    component.isOpen = true;
    component.loading = true;
    fixture.detectChanges();

    const loadingOverlay = fixture.nativeElement.querySelector('.modal-loading-overlay');
    expect(loadingOverlay).toBeTruthy();
  });

  it('should apply correct size class', () => {
    component.isOpen = true;
    component.size = 'large';
    fixture.detectChanges();

    const modal = fixture.nativeElement.querySelector('.modal');
    expect(modal.classList.contains('modal-large')).toBeTruthy();
  });

  it('should apply correct position class', () => {
    component.isOpen = true;
    component.position = 'top';
    fixture.detectChanges();

    const modal = fixture.nativeElement.querySelector('.modal');
    expect(modal.classList.contains('modal-top')).toBeTruthy();
  });

  it('should emit backdropClick when backdrop is clicked', () => {
    component.isOpen = true;
    component.backdropClose = true;
    fixture.detectChanges();

    spyOn(component, 'onBackdropClick');

    const backdrop = fixture.nativeElement.querySelector('.modal-backdrop');
    backdrop.click();

    expect(component.onBackdropClick).toHaveBeenCalled();
  });
});
