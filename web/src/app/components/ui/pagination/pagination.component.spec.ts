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
    hasPrevious: true,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginationComponent],
    }).compileComponents();

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

    component.selectedPageSize = 20;
    component.onPageSizeChange();

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

    const navButtons = fixture.nativeElement.querySelectorAll('.pagination-btn-nav');
    const lastButton = navButtons[navButtons.length - 1];
    expect(lastButton).toBeTruthy();
    expect(lastButton?.disabled).toBeTruthy();
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

  describe('getPageNumbers', () => {
    it('should return all pages when total pages is less than maxVisiblePages', () => {
      component.paginationInfo = { ...mockPaginationInfo, totalPages: 3 };
      component.maxVisiblePages = 5;

      const pages = component.getPageNumbers();
      expect(pages).toEqual([0, 1, 2]);
    });

    it('should return visible pages when total pages exceeds maxVisiblePages', () => {
      component.paginationInfo = { ...mockPaginationInfo, totalPages: 10, page: 5 };
      component.maxVisiblePages = 5;

      const pages = component.getPageNumbers();
      expect(pages.length).toBe(5);
      expect(pages).toContain(3);
      expect(pages).toContain(4);
      expect(pages).toContain(5);
      expect(pages).toContain(6);
      expect(pages).toContain(7);
    });

    it('should handle edge case when page is near the beginning', () => {
      component.paginationInfo = { ...mockPaginationInfo, totalPages: 10, page: 1 };
      component.maxVisiblePages = 5;

      const pages = component.getPageNumbers();
      expect(pages).toEqual([0, 1, 2, 3, 4]);
    });

    it('should handle edge case when page is near the end', () => {
      component.paginationInfo = { ...mockPaginationInfo, totalPages: 10, page: 8 };
      component.maxVisiblePages = 5;

      const pages = component.getPageNumbers();
      expect(pages).toEqual([5, 6, 7, 8, 9]);
    });

    it('should return empty array when paginationInfo is null', () => {
      component.paginationInfo = null;

      const pages = component.getPageNumbers();
      expect(pages).toEqual([]);
    });
  });

  describe('goToPage', () => {
    it('should emit pageChange when page is valid', () => {
      spyOn(component.pageChange, 'emit');
      component.paginationInfo = { ...mockPaginationInfo, totalPages: 5 };

      component.goToPage(2);

      expect(component.pageChange.emit).toHaveBeenCalledWith(2);
    });

    it('should not emit pageChange when page is invalid', () => {
      spyOn(component.pageChange, 'emit');
      component.paginationInfo = { ...mockPaginationInfo, totalPages: 5 };

      component.goToPage(-1);
      component.goToPage(10);

      expect(component.pageChange.emit).not.toHaveBeenCalled();
    });

    it('should not emit pageChange when paginationInfo is null', () => {
      spyOn(component.pageChange, 'emit');
      component.paginationInfo = null;

      component.goToPage(1);

      expect(component.pageChange.emit).not.toHaveBeenCalled();
    });
  });

  describe('navigation methods', () => {
    beforeEach(() => {
      spyOn(component, 'goToPage');
    });

    it('should go to first page', () => {
      component.goToFirstPage();
      expect(component.goToPage).toHaveBeenCalledWith(0);
    });

    it('should go to last page', () => {
      component.paginationInfo = { ...mockPaginationInfo, totalPages: 10 };
      component.goToLastPage();
      expect(component.goToPage).toHaveBeenCalledWith(9);
    });

    it('should go to previous page when hasPrevious is true', () => {
      component.paginationInfo = { ...mockPaginationInfo, hasPrevious: true, page: 2 };
      component.goToPreviousPage();
      expect(component.goToPage).toHaveBeenCalledWith(1);
    });

    it('should not go to previous page when hasPrevious is false', () => {
      component.paginationInfo = { ...mockPaginationInfo, hasPrevious: false };
      component.goToPreviousPage();
      expect(component.goToPage).not.toHaveBeenCalled();
    });

    it('should go to next page when hasNext is true', () => {
      component.paginationInfo = { ...mockPaginationInfo, hasNext: true, page: 2 };
      component.goToNextPage();
      expect(component.goToPage).toHaveBeenCalledWith(3);
    });

    it('should not go to next page when hasNext is false', () => {
      component.paginationInfo = { ...mockPaginationInfo, hasNext: false };
      component.goToNextPage();
      expect(component.goToPage).not.toHaveBeenCalled();
    });
  });

  describe('display methods', () => {
    it('should get display page number (1-based)', () => {
      expect(component.getDisplayPageNumber(0)).toBe(1);
      expect(component.getDisplayPageNumber(4)).toBe(5);
    });

    it('should get total elements', () => {
      expect(component.getTotalElements()).toBe(95);
      component.paginationInfo = null;
      expect(component.getTotalElements()).toBe(0);
    });

    it('should get current page (1-based)', () => {
      expect(component.getCurrentPage()).toBe(3); // page 2 + 1 = 3
      component.paginationInfo = null;
      expect(component.getCurrentPage()).toBe(0);
    });

    it('should get total pages', () => {
      expect(component.getTotalPages()).toBe(10);
      component.paginationInfo = null;
      expect(component.getTotalPages()).toBe(0);
    });

    it('should get page size', () => {
      expect(component.getPageSize()).toBe(10);
      component.paginationInfo = null;
      expect(component.getPageSize()).toBe(0);
    });

    it('should get start element', () => {
      expect(component.getStartElement()).toBe(21); // page 2 * size 10 + 1
      component.paginationInfo = { ...mockPaginationInfo, page: 0 };
      expect(component.getStartElement()).toBe(1); // page 0 * size 10 + 1
      component.paginationInfo = null;
      expect(component.getStartElement()).toBe(0);
    });

    it('should get end element', () => {
      expect(component.getEndElement()).toBe(30); // page 2 * size 10 + 10
      component.paginationInfo = { ...mockPaginationInfo, page: 0 };
      expect(component.getEndElement()).toBe(10); // page 0 * size 10 + 10
      component.paginationInfo = { ...mockPaginationInfo, page: 9, totalElements: 95 };
      expect(component.getEndElement()).toBe(95); // min(100, 95)
      component.paginationInfo = null;
      expect(component.getEndElement()).toBe(0);
    });
  });

  describe('ngOnChanges', () => {
    it('should update selectedPageSize when paginationInfo changes', () => {
      const newPaginationInfo = { ...mockPaginationInfo, size: 20 };
      component.paginationInfo = newPaginationInfo;
      component.ngOnChanges({
        paginationInfo: {
          currentValue: newPaginationInfo,
          previousValue: mockPaginationInfo,
          firstChange: false,
          isFirstChange: () => false
        }
      });

      expect(component.selectedPageSize).toBe(20);
    });

    it('should not update selectedPageSize when paginationInfo is null', () => {
      component.selectedPageSize = 15;
      component.paginationInfo = null;
      component.ngOnChanges({
        paginationInfo: {
          currentValue: null,
          previousValue: mockPaginationInfo,
          firstChange: false,
          isFirstChange: () => false
        }
      });

      expect(component.selectedPageSize).toBe(15);
    });
  });
});
