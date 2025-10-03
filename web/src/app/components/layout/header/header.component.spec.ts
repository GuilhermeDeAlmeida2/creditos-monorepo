import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display default title and subtitle', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Sistema de Créditos Constituídos');
    expect(compiled.querySelector('p')?.textContent).toContain(
      'Gerenciamento e consulta de créditos por NFS-e'
    );
  });

  it('should display custom title and subtitle when provided', () => {
    component.title = 'Custom Title';
    component.subtitle = 'Custom Subtitle';
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Custom Title');
    expect(compiled.querySelector('p')?.textContent).toContain('Custom Subtitle');
  });
});
