import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PaginationComponent, PaginationInfo } from './pagination.component';

describe('PaginationComponent', () => {
  let component: PaginationComponent;
  let fixture: ComponentFixture<PaginationComponent>;

  const mockPaginationInfo: PaginationInfo = {
    page: 2,
    size: 10,
    totalElements: 95,
    totalPages: 10,
    first: false,
    last: false,
    hasNext: true,
    hasPrevious: true
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaginationComponent);
    component = fixture.componentInstance;
    component.paginationInfo = mockPaginationInfo;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display pagination info', () => {
    const infoText = fixture.nativeElement.querySelector('.info-text');
    expect(infoText.textContent).toContain('Mostrando 21 - 30 de 95 itens');
  });

  it('should display current page info', () => {
    const pageInfo = fixture.nativeElement.querySelector('.page-info');
    expect(pageInfo.textContent).toContain('Página 3 de 10');
  });

  it('should emit page change when page button is clicked', () => {
    spyOn(component.pageChange, 'emit');
    
    const pageButton = fixture.nativeElement.querySelector('.pagination-btn-number');
    pageButton.click();
    
    expect(component.pageChange.emit).toHaveBeenCalled();
  });

  it('should emit page size change when select changes', () => {
    spyOn(component.pageSizeChange, 'emit');
    
    const select = fixture.nativeElement.querySelector('.page-size-select');
    select.value = '20';
    select.dispatchEvent(new Event('change'));
    
    expect(component.pageSizeChange.emit).toHaveBeenCalledWith(20);
  });

  it('should disable first button when on first page', () => {
    component.paginationInfo = { ...mockPaginationInfo, first: true };
    fixture.detectChanges();
    
    const firstButton = fixture.nativeElement.querySelector('.pagination-btn-nav');
    expect(firstButton.disabled).toBeTruthy();
  });

  it('should disable last button when on last page', () => {
    component.paginationInfo = { ...mockPaginationInfo, last: true };
    fixture.detectChanges();
    
    const lastButton = fixture.nativeElement.querySelectorAll('.pagination-btn-nav')[4];
    expect(lastButton.disabled).toBeTruthy();
  });

  it('should highlight current page', () => {
    const activeButton = fixture.nativeElement.querySelector('.pagination-btn.active');
    expect(activeButton).toBeTruthy();
    expect(activeButton.textContent.trim()).toBe('3');
  });

  it('should generate correct page numbers', () => {
    const pageNumbers = component.getPageNumbers();
    expect(pageNumbers).toEqual([0, 1, 2, 3, 4]); // Páginas 1-5 para página atual 3
  });

  it('should calculate start and end elements correctly', () => {
    expect(component.getStartElement()).toBe(21);
    expect(component.getEndElement()).toBe(30);
  });

  it('should not render when paginationInfo is null', () => {
    component.paginationInfo = null;
    fixture.detectChanges();
    
    const container = fixture.nativeElement.querySelector('.pagination-container');
    expect(container).toBeFalsy();
  });

  it('should hide info when showInfo is false', () => {
    component.showInfo = false;
    fixture.detectChanges();
    
    const info = fixture.nativeElement.querySelector('.pagination-info');
    expect(info).toBeFalsy();
  });

  it('should hide page size selector when showPageSizeSelector is false', () => {
    component.showPageSizeSelector = false;
    fixture.detectChanges();
    
    const selector = fixture.nativeElement.querySelector('.page-size-selector');
    expect(selector).toBeFalsy();
  });
});
