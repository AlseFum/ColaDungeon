// 简单的测试框架实现
class TinyTest {
    constructor() {
        this.tests = [];
        this.passed = 0;
        this.failed = 0;
    }

    // 注册测试用例
    test(name, fn) {
        this.tests.push({ name, fn });
    }

    // 断言函数
    assert(condition, message) {
        if (!condition) {
            throw new Error(`Assertion failed: ${message}`);
        }
    }

    // 运行所有测试
    async run() {
        console.log('Running tests...\n');
        
        for (const test of this.tests) {
            try {
                await test.fn();
                console.log(`✓ ${test.name}`);
                this.passed++;
            } catch (error) {
                console.error(`✗ ${test.name}`);
                console.error(`  ${error.message}`);
                this.failed++;
            }
        }

        console.log('\nTest Summary:');
        console.log(`Passed: ${this.passed}`);
        console.log(`Failed: ${this.failed}`);
        console.log(`Total: ${this.tests.length}`);
    }
}

// 导出测试框架
export const test = new TinyTest();