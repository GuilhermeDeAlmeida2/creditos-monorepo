import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterComponent } from './footer.component';

describe('FooterComponent', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display default company name', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h3')?.textContent).toContain('Sistema de Créditos Constituídos');
  });

  it('should display custom company name when provided', () => {
    component.companyName = 'Custom Company';
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h3')?.textContent).toContain('Custom Company');
  });

  it('should display current year in copyright', () => {
    const currentYear = new Date().getFullYear();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain(currentYear.toString());
  });

  it('should display version when showVersion is true', () => {
    component.showVersion = true;
    component.version = '2.0.0';
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Versão 2.0.0');
  });

  it('should not display version when showVersion is false', () => {
    component.showVersion = false;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).not.toContain('Versão');
  });

  it('should display copyright when showCopyright is true', () => {
    component.showCopyright = true;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Todos os direitos reservados');
  });

  it('should not display copyright when showCopyright is false', () => {
    component.showCopyright = false;
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).not.toContain('Todos os direitos reservados');
  });

  it('should display technology badges', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Angular');
    expect(compiled.textContent).toContain('Spring Boot');
    expect(compiled.textContent).toContain('PostgreSQL');
  });

  it('should display footer links', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Documentação');
    expect(compiled.textContent).toContain('Suporte');
    expect(compiled.textContent).toContain('Contato');
  });
});
