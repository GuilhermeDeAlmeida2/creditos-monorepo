import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TableComponent, TableColumn, TableAction } from './table.component';

describe('TableComponent', () => {
  let component: TableComponent;
  let fixture: ComponentFixture<TableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display empty message when no data', () => {
    component.data = [];
    component.emptyMessage = 'Nenhum item encontrado';
    fixture.detectChanges();

    const emptyState = fixture.nativeElement.querySelector('.empty-state p');
    expect(emptyState.textContent).toContain('Nenhum item encontrado');
  });

  it('should display loading state', () => {
    component.loading = true;
    fixture.detectChanges();

    const loadingElement = fixture.nativeElement.querySelector('.table-loading');
    expect(loadingElement).toBeTruthy();
  });

  it('should emit sort event when column is clicked', () => {
    const column: TableColumn = { key: 'name', label: 'Nome', sortable: true };
    component.columns = [column];
    component.data = [{ name: 'Test' }];
    fixture.detectChanges();

    spyOn(component.sort, 'emit');
    
    const headerCell = fixture.nativeElement.querySelector('.sortable');
    headerCell.click();

    expect(component.sort.emit).toHaveBeenCalledWith({ column: 'name', direction: 'asc' });
  });

  it('should emit row click event', () => {
    const testData = [{ id: 1, name: 'Test' }];
    component.data = testData;
    fixture.detectChanges();

    spyOn(component.rowClick, 'emit');
    
    const row = fixture.nativeElement.querySelector('.table-row');
    row.click();

    expect(component.rowClick.emit).toHaveBeenCalledWith(testData[0]);
  });

  it('should handle row selection', () => {
    component.selectable = true;
    component.data = [{ id: 1, name: 'Test' }];
    fixture.detectChanges();

    spyOn(component.rowSelect, 'emit');
    
    const checkbox = fixture.nativeElement.querySelector('.row-checkbox');
    checkbox.click();

    expect(component.rowSelect.emit).toHaveBeenCalled();
  });

  it('should execute action when action button is clicked', () => {
    const mockAction: TableAction = {
      label: 'Edit',
      icon: '✏️',
      variant: 'primary',
      onClick: jasmine.createSpy('onClick')
    };
    
    component.actions = [mockAction];
    component.data = [{ id: 1, name: 'Test' }];
    fixture.detectChanges();

    const actionButton = fixture.nativeElement.querySelector('.action-btn');
    actionButton.click();

    expect(mockAction.onClick).toHaveBeenCalledWith({ id: 1, name: 'Test' });
  });
});
